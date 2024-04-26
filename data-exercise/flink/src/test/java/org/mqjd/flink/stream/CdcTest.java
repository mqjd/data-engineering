package org.mqjd.flink.stream;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import java.time.ZoneOffset;
import java.util.Properties;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mqjd.flink.containers.ContainerBaseTest;
import org.mqjd.flink.containers.ContainerType;
import org.mqjd.flink.containers.mysql.MySqlContainer;
import org.mqjd.flink.containers.mysql.UniqueDatabase;
import org.testcontainers.containers.KafkaContainer;

public class CdcTest extends ContainerBaseTest {

    private static final String TOPIC_NAME = "flink-cec-test";
    private static final short REPLICATION_FACTOR = 1;
    private static final short NUM_PARTITIONS = 1;

    private final MySqlContainer MYSQL_CONTAINER = getContainer(ContainerType.MYSQL);
    private final UniqueDatabase inventoryDatabase =
        new UniqueDatabase(getContainer(ContainerType.MYSQL), "hd", "hd_user", "hd_user_password");

    @BeforeClass
    public static void startContainers() {
        ContainerBaseTest.startContainers(ContainerType.MYSQL, ContainerType.KAFKA);
    }

    @Test
    @Ignore("Test ignored because it won't stop and is used for manual test")
    public void mysqlCdcToKafka() throws Exception {
        createTopic(TOPIC_NAME, NUM_PARTITIONS, REPLICATION_FACTOR);
        inventoryDatabase.createAndInitialize();
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        TableEnvironment tableEnv = StreamTableEnvironment.create(environment,
            EnvironmentSettings.newInstance().inStreamingMode().build());
        tableEnv.executeSql(
            "CREATE TABLE source_table (\n" +
                "  id int,\n" +
                "  user_id STRING,\n" +
                "  class_id int,\n" +
                "  name STRING,\n" +
                "  age int,\n" +
                "  create_time TIMESTAMP(3),\n" +
                "  create_by STRING,\n" +
                "  PRIMARY KEY (id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "  'connector' = 'mysql-cdc',\n" +
                "  'scan.incremental.snapshot.chunk.key-column' = 'id',\n" +
                "  'table-name' = 'user',\n" +
                "  'hostname' = '" + inventoryDatabase.getHost() + "',\n" +
                "  'port' = '" + inventoryDatabase.getDatabasePort() + "',\n" +
                "  'database-name' = '" + inventoryDatabase.getDatabaseName() + "',\n" +
                "  'username' = '" + inventoryDatabase.getUsername() + "',\n" +
                "  'password' = '" + inventoryDatabase.getPassword() + "'" +
                ")");

        tableEnv.executeSql(
            "CREATE TABLE target_table (\n" +
                "  id int,\n" +
                "  user_id STRING,\n" +
                "  class_id int,\n" +
                "  name STRING,\n" +
                "  age int,\n" +
                "  create_time TIMESTAMP(3),\n" +
                "  create_by STRING,\n" +
                "  PRIMARY KEY (id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "  'connector' = 'mysql-cdc',\n" +
                "  'scan.incremental.snapshot.chunk.key-column' = 'id',\n" +
                "  'table-name' = 'user',\n" +
                "  'hostname' = '" + inventoryDatabase.getHost() + "',\n" +
                "  'port' = '" + inventoryDatabase.getDatabasePort() + "',\n" +
                "  'database-name' = '" + inventoryDatabase.getDatabaseName() + "',\n" +
                "  'username' = '" + inventoryDatabase.getUsername() + "',\n" +
                "  'password' = '" + inventoryDatabase.getPassword() + "'" +
                ")");
    }

    @Test
    @Ignore("Test ignored because it won't stop and is used for manual test")
    public void testConsumingAllEvents() throws Exception {
        createTopic(TOPIC_NAME, NUM_PARTITIONS, REPLICATION_FACTOR);
        inventoryDatabase.createAndInitialize();
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
            .hostname(MYSQL_CONTAINER.getHost())
            .port(MYSQL_CONTAINER.getDatabasePort())
            .databaseList(inventoryDatabase.getDatabaseName())
            .username(inventoryDatabase.getUsername())
            .password(inventoryDatabase.getPassword())
            .serverTimeZone(ZoneOffset.UTC.getId())
            .tableList(inventoryDatabase.getDatabaseName() + "." + "user")
            .deserializer(new StringDebeziumDeserializationSchema())
            .build();

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "mysql-cdc-user")
            .sinkTo(getKafkaSink(TOPIC_NAME));
        JobClient jobClient = env.executeAsync("mysql cdc + kafka");
        JobID jobID = jobClient.getJobID();
    }

    private KafkaSink<String> getKafkaSink(String topic) {
        KafkaContainer container = getContainer(ContainerType.KAFKA);
        return KafkaSink.<String>builder()
            .setBootstrapServers(container.getBootstrapServers())
            .setKafkaProducerConfig(new Properties())
            .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
            .setRecordSerializer(
                KafkaRecordSerializationSchema.builder()
                    .setTopic(topic)
                    .setValueSerializationSchema(new SimpleStringSchema())
                    .build())
            .build();
    }
}
