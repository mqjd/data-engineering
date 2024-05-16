package org.mqjd.flink.jobs.chapter2.section1;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.mqjd.flink.jobs.chapter2.section1.config.Environment;
import org.mqjd.flink.jobs.chapter2.section1.config.EnvironmentParser;
import org.mqjd.flink.jobs.chapter2.section1.config.Sink;
import org.mqjd.flink.jobs.chapter2.section1.config.Source;

public class KafkaExactlyOnce {

    private static final String JOB_YAML = "conf/chapter2/section1/job.yaml";

    public static void main(String[] args) throws Exception {
        Environment environment = EnvironmentParser.parse(JOB_YAML, args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(
            environment.getJobConfig().getConfiguration());
        Source source = environment.getSource();
        Sink sink = environment.getSink();
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
            .setTopics(source.getTopics()).setProperties(source.getProps())
            .setStartingOffsets(OffsetsInitializer.earliest())
            .setValueOnlyDeserializer(new SimpleStringSchema()).build();

        KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
            .setTransactionalIdPrefix("chapter2-section1-")
            .setKafkaProducerConfig(sink.getProps()).setRecordSerializer(
                KafkaRecordSerializationSchema.builder().setTopic(sink.getTopic())
                    .setKeySerializationSchema(new SimpleStringSchema())
                    .setValueSerializationSchema(new SimpleStringSchema()).build())
            .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE).build();

        DataStreamSource<String> kafkaSourceStream = env.fromSource(kafkaSource,
            WatermarkStrategy.noWatermarks(), "Kafka Source");

        kafkaSourceStream.map(new TroubleMaker()).name("Trouble Maker").sinkTo(kafkaSink).name("Kafka Sink");
        env.execute("Kafka Exactly Once");
    }
}
