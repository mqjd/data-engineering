package org.mqjd.flink.jobs.chapter2.section1;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.containers.ContainerType;
import org.mqjd.flink.function.TroubleMaker;
import org.mqjd.flink.jobs.CommandArgs;
import org.mqjd.flink.util.TimerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;

public class KafkaExactlyOnceTest extends ContainerBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaExactlyOnceTest.class);

    private static final String TOPICS_IN = "hd-test-chapter2-section1-in";
    private static final String TOPICS_OUT = "hd-test-chapter2-section1-out";
    private static final String GROUP = "hd-test-chapter2-section1-out-consumer-test";
    private static final String CLIENT_ID = "hd-test-chapter2-section1-in-producer-test";
    private static final short REPLICATION_FACTOR = 1;
    private static final short NUM_PARTITIONS = 1;

    @BeforeClass
    public static void startContainers() {
        ContainerBaseTest.startContainers(ContainerType.KAFKA);
    }

    @Test
    public void given_correct_input_and_output_when_execute_then_success() throws Exception {
        createTopic(TOPICS_IN, NUM_PARTITIONS, REPLICATION_FACTOR);
        createTopic(TOPICS_OUT, NUM_PARTITIONS, REPLICATION_FACTOR);
        KafkaContainer container = getContainer(ContainerType.KAFKA);
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        executeJobAsync(() -> {
            try {
                String bootstrapServers = container.getBootstrapServers();
                String[] params = CommandArgs.builder()
                    .defaultKey("D")
                    .kvOption("source.property.bootstrap.servers", bootstrapServers)
                    .kvOption("sink.property.bootstrap.servers", bootstrapServers)
                    .build();
                KafkaExactlyOnce.main(params);
            } catch (Exception e) {
                LOG.error("Error execute KafkaExactlyOnce", e);
            }
        }, jobStatus -> {
            if (jobStatus.equals(JobStatus.RUNNING)) {
                TimerUtil.timeout(() -> TroubleMaker.makeTrouble(new RuntimeException("interrupted by test")), 10_000L);
            } else if (jobStatus.isTerminalState()) {
                result.complete(true);
            }
        });
        kafkaProduce(TOPICS_IN, CLIENT_ID, 10L, (i) -> new Tuple2<>(i.toString(), "message" + i));
        Collection<String> messages = new ConcurrentLinkedQueue<>();
        kafkaConsume(TOPICS_OUT, GROUP, (record) -> {
            LOG.info("result: offset: {}, message: {}", record.offset(), record.value());
            messages.add(record.value());
            return true;
        });
        result.get();

        long expectedCount =
            Long.parseLong(new ArrayList<>(messages).get(messages.size() - 1).substring("message".length()));
        assertEquals(expectedCount, messages.size());
        assertEquals(expectedCount, new HashSet<>(messages).size());
    }
}