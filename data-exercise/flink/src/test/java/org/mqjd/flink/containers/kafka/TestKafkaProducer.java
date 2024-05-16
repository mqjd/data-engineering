package org.mqjd.flink.containers.kafka;

import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.mqjd.flink.util.TimerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.lifecycle.Startable;

public class TestKafkaProducer implements Runnable, Startable {

    private static final Logger LOG = LoggerFactory.getLogger(TestKafkaProducer.class);

    private final KafkaProducer<String, String> kafkaProducer;
    private final String topic;
    private final Long period;
    private final Long rate;
    private final Function<Long, String> messageGenerator;
    private final AtomicLong counter = new AtomicLong(0);
    private boolean running = false;
    private ScheduledFuture<?> schedule;

    public TestKafkaProducer(Function<Long, String> messageGenerator, String clientId, String topic, Long period, Long rate,
        KafkaContainer kafkaContainer) {
        this.period = period;
        this.rate = rate;
        this.topic = topic;
        this.messageGenerator = messageGenerator;
        Properties properties = new Properties();
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
            kafkaContainer.getBootstrapServers());
        StringSerializer serializer = new StringSerializer();
        kafkaProducer = new KafkaProducer<>(properties, serializer, serializer);
    }

    @Override
    public void run() {
        for (int i = 0; i < rate; i++) {
            String message = messageGenerator.apply(counter.incrementAndGet());
            kafkaProducer.send(new ProducerRecord<>(topic, message), (metadata, exception) -> {
                if (exception != null) {
                    throw new RuntimeException("Error sending message", exception);
                }
                LOG.info("send message to topic: {}, offset: {}", topic, metadata.offset());
            });
        }
    }

    @Override
    public void start() {
        if (running) {
            throw new IllegalStateException("KafkaProducer already running");
        }
        this.running = true;
        schedule = TimerUtil.interval(this, period);
    }

    @Override
    public void close() {
        kafkaProducer.close();
    }

    @Override
    public void stop() {
        schedule.cancel(false);
        this.running = false;
    }
}
