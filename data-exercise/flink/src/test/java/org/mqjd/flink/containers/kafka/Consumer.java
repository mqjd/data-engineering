package org.mqjd.flink.containers.kafka;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;
import java.util.function.BiFunction;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.lifecycle.Startable;

public class Consumer implements Runnable, Startable {
    private final KafkaConsumer<String, String> consumer;
    private final BiFunction<String, String, Boolean> messageConsumer;
    private boolean running = false;

    public Consumer(BiFunction<String, String, Boolean> messageConsumer, String topic, String groupId,
        KafkaContainer kafkaContainer) {
        this.messageConsumer = messageConsumer;
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        // properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        Deserializer<String> deserializer = new StringDeserializer();
        consumer = new KafkaConsumer<>(properties, deserializer, deserializer);
        consumer.subscribe(Collections.singletonList(topic));
    }

    @Override
    public void start() {
        if (running) {
            throw new IllegalStateException("KafkaConsumer already running");
        }
        this.running = true;
        new Thread(this).start();
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public void close() {
        consumer.close();
    }

    @Override
    public void run() {
        ConsumerRecords<String, String> poll;
        while (running && (poll = consumer.poll(Duration.of(3, ChronoUnit.SECONDS))) != null) {
            for (ConsumerRecord<String, String> record : poll) {
                if (messageConsumer.apply(record.key(), record.value())) {
                    consumer.commitAsync();
                }
            }
        }
    }
}