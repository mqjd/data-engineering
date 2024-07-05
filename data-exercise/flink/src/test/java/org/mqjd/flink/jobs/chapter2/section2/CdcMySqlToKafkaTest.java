package org.mqjd.flink.jobs.chapter2.section2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.flink.core.execution.JobClient;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.TopicPartition;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.containers.ContainerType;
import org.mqjd.flink.containers.mysql.UniqueDatabase;
import org.mqjd.flink.jobs.CommandArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;

public class CdcMySqlToKafkaTest extends ContainerBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(CdcMySqlToKafkaTest.class);
    private static final String CHAPTER = "chapter2";
    private static final String SECTION = "section2";
    private static final String TOPIC = "hd-test-chapter2-section2";
    private static final String GROUP = "hd-test-chapter2-section2";
    private static final short REPLICATION_FACTOR = 1;
    private static final short NUM_PARTITIONS = 1;
    private final UniqueDatabase inventoryDatabase =
        new UniqueDatabase(getContainer(ContainerType.MYSQL), "chapter2_section2", "hd_user", "hd_user_password");

    @BeforeClass
    public static void startContainers() {
        ContainerBaseTest.startContainers(ContainerType.MYSQL, ContainerType.KAFKA);
    }

    @Test
    public void given_correct_input_and_output_when_execute_then_success() throws Exception {
        inventoryDatabase.createAndInitialize(CHAPTER, SECTION);
        createTopic(TOPIC, NUM_PARTITIONS, REPLICATION_FACTOR);
        KafkaContainer kafkaContainer = getContainer(ContainerType.KAFKA);
        CompletableFuture<JobClient> jobClientFuture = executeJobAsync(() -> {
            try {
                String[] params = CommandArgs.builder()
                    .defaultKey("D")
                    .kvOption("source.port", inventoryDatabase.getDatabasePort())
                    .kvOption("source.hostname", inventoryDatabase.getHost())
                    .kvOption("source.username", inventoryDatabase.getUsername())
                    .kvOption("source.password", inventoryDatabase.getPassword())
                    .kvOption("source.database-name", inventoryDatabase.getDatabaseName())
                    .kvOption("source.table-name", inventoryDatabase.getDatabaseName() + ".user")
                    .kvOption("sink.property.bootstrap.servers", kafkaContainer.getBootstrapServers())
                    .build();
                CdcMySqlToKafka.main(params);
            } catch (Exception e) {
                LOG.error("Error execute CdcMySqlToKafka", e);
            }
        });
        int messageCount = 20;
        CountDownLatch countDownLatch = new CountDownLatch(messageCount);
        kafkaConsume(TOPIC, GROUP, (record) -> {
            countDownLatch.countDown();
            LOG.info("key: {}, value: {}", record.key(), record.value());
            return true;
        });
        boolean await = countDownLatch.await(3, TimeUnit.MINUTES);
        assertTrue(await);
        jobClientFuture.get().cancel().get(10, TimeUnit.SECONDS);
        AdminClient adminClient = getAdminClient();
        TopicPartition topicPartition = new TopicPartition(TOPIC, 0);
        ListOffsetsResult.ListOffsetsResultInfo resultInfo =
            adminClient.listOffsets(Collections.singletonMap(topicPartition, OffsetSpec.latest()))
                .all()
                .get()
                .get(topicPartition);
        assertEquals(messageCount + 1, resultInfo.offset());
    }
}