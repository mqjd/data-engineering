package org.mqjd.flink.jobs.chapter2.section2;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import java.time.ZoneOffset;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.mqjd.flink.env.Environment;
import org.mqjd.flink.env.EnvironmentParser;
import org.mqjd.flink.env.node.sink.KafkaSinkNode;
import org.mqjd.flink.env.node.source.MySqlCdcNode;
import org.mqjd.flink.function.TroubleMaker;

public class CdcMySqlToKafka {

    private static final String JOB_YAML = "conf/chapter2/section2/job.yaml";

    public static void main(String[] args) throws Exception {
        Environment environment = EnvironmentParser.parse(JOB_YAML, args);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(
            environment.getJobConfig().getConfiguration());
        MySqlCdcNode source = environment.getSource();
        KafkaSinkNode sink = environment.getSink();

        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
            .hostname(source.getHostname())
            .port(source.getPort())
            .databaseList(source.getDatabaseName())
            .tableList(source.getTableName())
            .username(source.getUsername())
            .password(source.getPassword())
            .deserializer(new JsonDebeziumDeserializationSchema())
            .serverTimeZone(source.getProperty("server-time-zone", ZoneOffset.UTC.getId()))
            .build();

        KafkaSink<String> kafkaSink = KafkaSink.<String>builder()
            .setTransactionalIdPrefix("chapter2-section1-").setKafkaProducerConfig(sink.getProps())
            .setRecordSerializer(KafkaRecordSerializationSchema.builder().setTopic(sink.getTopic())
                .setKeySerializationSchema(new SimpleStringSchema())
                .setValueSerializationSchema(new SimpleStringSchema()).build())
            .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE).build();

        DataStreamSource<String> cdcSourceStream = env.fromSource(mySqlSource,
            WatermarkStrategy.noWatermarks(), "MySql CDC Source");

        cdcSourceStream.map(new TroubleMaker<>()).name("Trouble Maker").sinkTo(kafkaSink)
            .name("Kafka Sink");
        env.execute("MySql Cdc To Kafka");
    }

}
