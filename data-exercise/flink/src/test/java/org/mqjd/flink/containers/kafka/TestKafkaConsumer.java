package org.mqjd.flink.containers.kafka;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.mqjd.flink.util.TimerUtil;
import org.testcontainers.lifecycle.Startable;

public class TestKafkaConsumer<K, V> implements Runnable, Startable {

    private final KafkaConsumer<K, V> consumer;
    private final Function<ConsumerRecord<K, V>, Boolean> messageConsumer;
    private boolean running = false;
    private ScheduledFuture<?> schedule;

    TestKafkaConsumer(String bootstrapServers, String topic, String groupId,
        Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer,
        Function<ConsumerRecord<K, V>, Boolean> messageConsumer) {
        this.messageConsumer = messageConsumer;
        Properties properties = new Properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 5000);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumer = new KafkaConsumer<>(properties, keyDeserializer, valueDeserializer);
        consumer.subscribe(Collections.singletonList(topic));
    }

    @Override
    public void start() {
        if (running) {
            throw new IllegalStateException("KafkaConsumer already running");
        }
        this.running = true;
        schedule = TimerUtil.interval(this, 1000);
    }

    @Override
    public void stop() {
        this.running = false;
        schedule.cancel(false);
    }

    @Override
    public void close() {
        consumer.close();
    }

    @Override
    public void run() {
        ConsumerRecords<K, V> poll = consumer.poll(Duration.of(3, ChronoUnit.SECONDS));
        for (ConsumerRecord<K, V> record : poll) {
            if (messageConsumer.apply(record)) {
                consumer.commitAsync();
            }
        }
    }
}
