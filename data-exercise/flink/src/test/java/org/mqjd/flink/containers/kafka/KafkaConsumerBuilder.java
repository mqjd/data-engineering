package org.mqjd.flink.containers.kafka;

import static org.apache.flink.shaded.guava31.com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class KafkaConsumerBuilder<K, V> {

    private String topic;
    private String groupId;
    private String bootstrapServers;
    private Deserializer<K> keyDeserializer;
    private Deserializer<V> valueDeserializer;
    private Function<ConsumerRecord<K, V>, Boolean> messageConsumer;

    public static <K, V> KafkaConsumerBuilder<K, V> builder() {
        return new KafkaConsumerBuilder<>();
    }

    public static KafkaConsumerBuilder<String, String> stringBuilder() {
        KafkaConsumerBuilder<String, String> builder = new KafkaConsumerBuilder<>();
        StringDeserializer deserializer = new StringDeserializer();
        builder.withKeyDeserializer(deserializer);
        builder.withValueDeserializer(deserializer);
        return builder;
    }

    public KafkaConsumerBuilder<K, V> withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public KafkaConsumerBuilder<K, V> withGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public KafkaConsumerBuilder<K, V> withBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
        return this;
    }

    public KafkaConsumerBuilder<K, V> withKeyDeserializer(Deserializer<K> keyDeserializer) {
        this.keyDeserializer = keyDeserializer;
        return this;
    }


    public KafkaConsumerBuilder<K, V> withValueDeserializer(Deserializer<V> valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
        return this;
    }

    public KafkaConsumerBuilder<K, V> withMessageConsumer(
        Function<ConsumerRecord<K, V>, Boolean> messageConsumer) {
        this.messageConsumer = messageConsumer;
        return this;
    }

    public TestKafkaConsumer<K, V> build() {
        checkNotNull(bootstrapServers, "bootstrapServers is required");
        checkNotNull(topic, "topic is required");
        checkNotNull(groupId, "groupId is required");
        checkNotNull(keyDeserializer, "keyDeserializer is required");
        checkNotNull(valueDeserializer, "valueDeserializer is required");
        checkNotNull(messageConsumer, "messageConsumer is required");
        return new TestKafkaConsumer<>(bootstrapServers, topic, groupId, keyDeserializer,
            valueDeserializer, messageConsumer);
    }

}
