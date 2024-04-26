package org.mqjd.flink.containers.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.flink.util.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;

/**
 * Collection of methods to interact with a Kafka cluster.
 */
public class KafkaUtil {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaUtil.class);
    private static final Duration CONSUMER_POLL_DURATION = Duration.ofSeconds(1);

    private KafkaUtil() {
    }

    public static KafkaContainer createKafkaContainer(String dockerImageVersion, Logger logger) {
        return createKafkaContainer(dockerImageVersion, logger, null);
    }


    public static KafkaContainer createKafkaContainer(
        String dockerImageVersion, Logger logger, String loggerPrefix) {
        String logLevel;
        if (logger.isTraceEnabled()) {
            logLevel = "TRACE";
        } else if (logger.isDebugEnabled()) {
            logLevel = "DEBUG";
        } else if (logger.isInfoEnabled()) {
            logLevel = "INFO";
        } else if (logger.isWarnEnabled()) {
            logLevel = "WARN";
        } else if (logger.isErrorEnabled()) {
            logLevel = "ERROR";
        } else {
            logLevel = "OFF";
        }

        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        if (!StringUtils.isNullOrWhitespaceOnly(loggerPrefix)) {
            logConsumer.withPrefix(loggerPrefix);
        }
        return new KafkaContainer(DockerImageName.parse(dockerImageVersion))
            .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1")
            .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1")
            .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
            .withEnv("KAFKA_CONFLUENT_SUPPORT_METRICS_ENABLE", "false")
            .withEnv("KAFKA_LOG4J_ROOT_LOGLEVEL", logLevel)
            .withEnv("KAFKA_LOG4J_LOGGERS", "state.change.logger=" + logLevel)
            .withEnv("KAFKA_CONFLUENT_SUPPORT_METRICS_ENABLE", "false")
            .withEnv(
                "KAFKA_TRANSACTION_MAX_TIMEOUT_MS",
                String.valueOf(Duration.ofHours(2).toMillis()))
            .withEnv("KAFKA_LOG4J_TOOLS_ROOT_LOGLEVEL", logLevel)
            .withLogConsumer(logConsumer);
    }

    @SuppressWarnings("unused")
    public static List<ConsumerRecord<byte[], byte[]>> drainAllRecordsFromTopic(
        String topic, Properties properties, boolean committed) throws KafkaException {
        final Properties consumerConfig = new Properties();
        consumerConfig.putAll(properties);
        consumerConfig.put(
            ConsumerConfig.ISOLATION_LEVEL_CONFIG,
            committed ? "read_committed" : "read_uncommitted");
        return drainAllRecordsFromTopic(topic, consumerConfig);
    }

    public static List<ConsumerRecord<byte[], byte[]>> drainAllRecordsFromTopic(
        String topic, Properties properties) throws KafkaException {
        final Properties consumerConfig = new Properties();
        consumerConfig.putAll(properties);
        consumerConfig.put("key.deserializer", ByteArrayDeserializer.class.getName());
        consumerConfig.put("value.deserializer", ByteArrayDeserializer.class.getName());
        try (KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(consumerConfig)) {
            Set<TopicPartition> topicPartitions = getAllPartitions(consumer, topic);
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(topicPartitions);
            consumer.assign(topicPartitions);
            consumer.seekToBeginning(topicPartitions);

            final List<ConsumerRecord<byte[], byte[]>> consumerRecords = new ArrayList<>();
            while (!topicPartitions.isEmpty()) {
                ConsumerRecords<byte[], byte[]> records = consumer.poll(CONSUMER_POLL_DURATION);
                LOG.debug("Fetched {} records from topic {}.", records.count(), topic);

                // Remove partitions from polling which have reached its end.
                final List<TopicPartition> finishedPartitions = new ArrayList<>();
                for (final TopicPartition topicPartition : topicPartitions) {
                    final long position = consumer.position(topicPartition);
                    final long endOffset = endOffsets.get(topicPartition);
                    LOG.debug(
                        "Endoffset {} and current position {} for partition {}",
                        endOffset,
                        position,
                        topicPartition.partition());
                    if (endOffset - position > 0) {
                        continue;
                    }
                    finishedPartitions.add(topicPartition);
                }

                // noinspection all
                if (topicPartitions.removeAll(finishedPartitions)) {
                    consumer.assign(topicPartitions);
                }
                for (ConsumerRecord<byte[], byte[]> r : records) {
                    consumerRecords.add(r);
                }
            }
            return consumerRecords;
        }
    }

    private static Set<TopicPartition> getAllPartitions(
        KafkaConsumer<byte[], byte[]> consumer, String topic) {
        return consumer.partitionsFor(topic).stream()
            .map(info -> new TopicPartition(info.topic(), info.partition()))
            .collect(Collectors.toSet());
    }
}

