package org.mqjd.flink.containers.kafka;

import static org.apache.flink.shaded.guava31.com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerBuilder<K, V> {

    private String bootstrapServers;
    private String topic;
    private String clientId;
    private Long period;
    private Long rate;
    private Serializer<K> keySerializer;
    private Serializer<V> valueSerializer;
    private Function<Long, Tuple2<K, V>> messageGenerator;

    public static <K, V> KafkaProducerBuilder<K, V> builder() {
        return new KafkaProducerBuilder<>();
    }

    public static KafkaProducerBuilder<String, String> stringBuilder() {
        KafkaProducerBuilder<String, String> builder = new KafkaProducerBuilder<>();
        StringSerializer serializer = new StringSerializer();
        builder.withKeySerializer(serializer);
        builder.withValueSerializer(serializer);
        return builder;
    }

    public KafkaProducerBuilder<K, V> withBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
        return this;
    }

    public KafkaProducerBuilder<K, V> withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public KafkaProducerBuilder<K, V> withClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public KafkaProducerBuilder<K, V> withPeriod(Long period) {
        this.period = period;
        return this;
    }

    public KafkaProducerBuilder<K, V> withRate(Long rate) {
        this.rate = rate;
        return this;
    }

    public KafkaProducerBuilder<K, V> withKeySerializer(Serializer<K> keySerializer) {
        this.keySerializer = keySerializer;
        return this;
    }

    public KafkaProducerBuilder<K, V> withValueSerializer(Serializer<V> valueSerializer) {
        this.valueSerializer = valueSerializer;
        return this;
    }

    public KafkaProducerBuilder<K, V> withMessageGenerator(
        Function<Long, Tuple2<K, V>> messageGenerator) {
        this.messageGenerator = messageGenerator;
        return this;
    }

    public TestKafkaProducer<K, V> build() {
        checkNotNull(bootstrapServers, "bootstrapServers is required");
        checkNotNull(topic, "topic is required");
        checkNotNull(clientId, "clientId is required");
        checkNotNull(period, "period is required");
        checkNotNull(rate, "rate is required");
        checkNotNull(keySerializer, "keyDeserializer is required");
        checkNotNull(valueSerializer, "valueDeserializer is required");
        checkNotNull(messageGenerator, "messageGenerator is required");
        return new TestKafkaProducer<>(bootstrapServers, topic, clientId, period, rate,
            keySerializer, valueSerializer, messageGenerator);
    }
}
