package org.mqjd.flink.containers;

import static org.apache.flink.util.DockerImageVersions.KAFKA;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.AfterClass;
import org.mqjd.flink.containers.kafka.Consumer;
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
    private static final List<Startable> STARTED_CONTAINERS = new ArrayList<>();
    private static final String INTER_CONTAINER_KAFKA_ALIAS = "kafka";
    private static final Network NETWORK = Network.newNetwork();
    private static AdminClient admin;

    public static void startContainers(ContainerType... types) {
        LOG.info("Starting containers...");
        List<Startable> startables = new ArrayList<>();
        for (ContainerType type : types) {
            if (CONTAINERS.containsKey(type)) {
                Startable startable = CONTAINERS.remove(type);
                startables.add(startable);
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
        LOG.info("Containers are stopped.");
    }

    protected void createTopic(String topic, int numPartitions, short replicationFactor)
        throws ExecutionException, InterruptedException {
        AdminClient adminClient = getAdminClient();
        final CreateTopicsResult result =
            adminClient.createTopics(Collections.singletonList(new NewTopic(topic, numPartitions, replicationFactor)));
        result.all().get();
    }

    protected void consume(String topic, String group, BiFunction<String, String, Boolean> messageConsumer) {
        Consumer consumer = new Consumer(messageConsumer, topic, group, getContainer(ContainerType.KAFKA));
        STARTED_CONTAINERS.addFirst(consumer);
        consumer.start();

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
            .withSetupSQL("docker/setup.sql")
            .withDatabaseName("flink-test")
            .withUsername("flink_user")
            .withPassword("flink_pw")
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
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        admin = AdminClient.create(properties);
        STARTED_CONTAINERS.add(new Closeable(admin));
        return admin;
    }

    private static Startable createKafka() {
        // noinspection resource
        return KafkaUtil.createKafkaContainer(KAFKA, LOG)
            .withEmbeddedZookeeper()
            .withNetwork(NETWORK)
            .withNetworkAliases(INTER_CONTAINER_KAFKA_ALIAS);
    }

}
