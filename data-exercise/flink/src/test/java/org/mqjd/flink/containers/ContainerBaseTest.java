package org.mqjd.flink.containers;

import static org.apache.flink.util.DockerImageVersions.KAFKA;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.AfterClass;
import org.mqjd.flink.containers.kafka.KafkaUtil;
import org.mqjd.flink.containers.mysql.MySqlContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.lifecycle.Startables;

public abstract class ContainerBaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerBaseTest.class);

    private static final Map<ContainerType, Startable> CONTAINERS = prepareContainers();
    private static final Set<ContainerType> USED_TYPES = new HashSet<>();
    private static final String INTER_CONTAINER_KAFKA_ALIAS = "kafka";
    private static final Network NETWORK = Network.newNetwork();
    private static AdminClient admin;

    public static void startContainers(ContainerType... types) {
        LOG.info("Starting containers...");
        USED_TYPES.addAll(Arrays.asList(types));
        Startables.deepStart(USED_TYPES.stream().map(CONTAINERS::get)).join();
        LOG.info("Containers are started.");
    }

    @AfterClass
    public static void stopContainers() {
        LOG.info("Stopping containers...");
        USED_TYPES.stream().map(CONTAINERS::get).forEach(Startable::stop);
        if (admin != null) {
            admin.close();
        }
        LOG.info("Containers are stopped.");
    }

    protected void createTopic(String topic, int numPartitions, short replicationFactor)
        throws ExecutionException, InterruptedException {
        AdminClient adminClient = createAdminClient();
        final CreateTopicsResult result =
            adminClient.createTopics(
                Collections.singletonList(
                    new NewTopic(topic, numPartitions, replicationFactor)));
        result.all().get();
    }

    protected <K, V> void consume(String topic, BiConsumer<K, V> messageConsumer) {
//        Map<String, String> properties = new HashMap<>();
//        KafkaContainer kafkaContainer = getContainer(ContainerType.KAFKA);
//        properties.put(
//            CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
//            kafkaContainer.getBootstrapServers());
//        KafkaConsumer<K, V> consumer = new KafkaConsumer<>(properties);
//        ConsumerRecords<K, V> poll;
//        consumer.subscribe(Collections.singletonList(topic));
//        while ((poll = consumer.poll(Duration.of(10, ChronoUnit.SECONDS))) != null) {
//            for (ConsumerRecord<K, V> record : poll) {
//                messageConsumer.accept(record.key(), record.value());
//            }
//        }

    }

    private static Map<ContainerType, Startable> prepareContainers() {
        Map<ContainerType, Startable> containers = new HashMap<>();
        containers.put(ContainerType.MYSQL, createMySQL());
        containers.put(ContainerType.KAFKA, createKafka());
        return containers;
    }

    private static Startable createMySQL() {
        //noinspection all
        return new MySqlContainer()
            .withConfigurationOverride("docker/server/my.cnf")
            .withSetupSQL("docker/setup.sql")
            .withDatabaseName("flink-test")
            .withUsername("flink_user")
            .withPassword("flink_pw")
            .withLogConsumer(new Slf4jLogConsumer(LOG));
    }

    protected static <T> T getContainer(ContainerType type) {
        //noinspection unchecked
        return (T) CONTAINERS.get(type);
    }

    protected AdminClient createAdminClient() {
        if (admin != null) {
            return admin;
        }
        Map<String, Object> properties = new HashMap<>();
        KafkaContainer kafkaContainer = getContainer(ContainerType.KAFKA);
        properties.put(
            CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
            kafkaContainer.getBootstrapServers());
        admin = AdminClient.create(properties);
        return admin;
    }

    private static Startable createKafka() {
        return KafkaUtil.createKafkaContainer(KAFKA, LOG)
            .withEmbeddedZookeeper()
            .withNetwork(NETWORK)
            .withNetworkAliases(INTER_CONTAINER_KAFKA_ALIAS);
    }

}
