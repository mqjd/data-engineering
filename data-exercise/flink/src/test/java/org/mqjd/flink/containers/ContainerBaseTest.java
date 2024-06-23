package org.mqjd.flink.containers;

import static org.apache.flink.util.DockerImageVersions.KAFKA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.AfterClass;
import org.mqjd.flink.containers.kafka.KafkaConsumerBuilder;
import org.mqjd.flink.containers.kafka.KafkaProducerBuilder;
import org.mqjd.flink.containers.kafka.KafkaUtil;
import org.mqjd.flink.containers.kafka.TestKafkaConsumer;
import org.mqjd.flink.containers.kafka.TestKafkaProducer;
import org.mqjd.flink.containers.mysql.MySqlContainer;
import org.mqjd.flink.jobs.FlinkJobTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.lifecycle.Startables;

public abstract class ContainerBaseTest extends FlinkJobTest {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerBaseTest.class);
    private static final Map<ContainerType, Startable> CONTAINERS = prepareContainers();
    private static final List<Startable> STARTED_CONTAINERS = new ArrayList<>();
    private static final String INTER_CONTAINER_KAFKA_ALIAS = "kafka";
    private static final Network NETWORK = Network.newNetwork();
    private static AdminClient admin;

    public static void startContainers(ContainerType... types) {
        LOG.info("Starting containers...");
        List<Startable> startables = new ArrayList<>();
        for (ContainerType type : types) {
            if (CONTAINERS.containsKey(type)) {
                Startable startable = CONTAINERS.get(type);
                if (!STARTED_CONTAINERS.contains(startable)) {
                    startables.add(startable);
                }
            }
        }
        if (!startables.isEmpty()) {
            STARTED_CONTAINERS.addAll(startables);
            Startables.deepStart(startables.stream()).join();
        }
        LOG.info("Containers are started.");
    }

    @AfterClass
    public static void stopContainers() {
        LOG.info("Stopping containers...");
        STARTED_CONTAINERS.forEach(Startable::stop);
        STARTED_CONTAINERS.clear();
        admin = null;
        LOG.info("Containers are stopped.");
    }

    protected void createTopic(String topic, int numPartitions, short replicationFactor)
        throws ExecutionException, InterruptedException {
        AdminClient adminClient = getAdminClient();
        final CreateTopicsResult result = adminClient.createTopics(
            Collections.singletonList(new NewTopic(topic, numPartitions, replicationFactor)));
        result.all().get();
    }

    protected void kafkaConsume(String topic, String group,
        Function<ConsumerRecord<String, String>, Boolean> messageConsumer) {
        KafkaContainer kafkaContainer = getContainer(ContainerType.KAFKA);
        StringDeserializer deserializer = new StringDeserializer();
        TestKafkaConsumer<String, String> testKafkaConsumer = KafkaConsumerBuilder.<String, String>builder()
            .withBootstrapServers(kafkaContainer.getBootstrapServers()).withTopic(topic)
            .withGroupId(group).withKeyDeserializer(deserializer)
            .withValueDeserializer(deserializer).withMessageConsumer(messageConsumer).build();
        STARTED_CONTAINERS.addFirst(testKafkaConsumer);
        testKafkaConsumer.start();
    }

    protected void kafkaProduce(String topic, String clientId, Long rate,
        Function<Long, Tuple2<String, String>> messageGenerator) {
        KafkaContainer kafkaContainer = getContainer(ContainerType.KAFKA);
        StringSerializer stringSerializer = new StringSerializer();
        TestKafkaProducer<String, String> testKafkaProducer = KafkaProducerBuilder.<String, String>builder()
            .withBootstrapServers(kafkaContainer.getBootstrapServers()).withTopic(topic)
            .withClientId(clientId).withPeriod(1000L).withRate(rate)
            .withKeySerializer(stringSerializer).withValueSerializer(stringSerializer)
            .withMessageGenerator(messageGenerator).build();
        STARTED_CONTAINERS.addFirst(testKafkaProducer);
        testKafkaProducer.start();
    }

    private static Map<ContainerType, Startable> prepareContainers() {
        Map<ContainerType, Startable> containers = new HashMap<>();
        containers.put(ContainerType.MYSQL, createMySQL());
        containers.put(ContainerType.KAFKA, createKafka());
        return containers;
    }

    private static Startable createMySQL() {
        // noinspection all
        return new MySqlContainer().withConfigurationOverride("docker/server/my.cnf")
            .withSetupSQL("docker/setup.sql").withDatabaseName("flink-test")
            .withUsername("flink_user").withPassword("flink_pw")
            .withLogConsumer(new Slf4jLogConsumer(LOG));
    }

    protected static <T> T getContainer(ContainerType type) {
        // noinspection unchecked
        return (T) CONTAINERS.get(type);
    }

    protected AdminClient getAdminClient() {
        if (admin != null) {
            return admin;
        }
        Map<String, Object> properties = new HashMap<>();
        KafkaContainer kafkaContainer = getContainer(ContainerType.KAFKA);
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
            kafkaContainer.getBootstrapServers());
        admin = AdminClient.create(properties);
        STARTED_CONTAINERS.add(new Closeable(admin));
        return admin;
    }

    private static Startable createKafka() {
        // noinspection resource
        return KafkaUtil.createKafkaContainer(KAFKA, LOG).withEmbeddedZookeeper()
            .withNetwork(NETWORK).withNetworkAliases(INTER_CONTAINER_KAFKA_ALIAS);
    }

}
