package org.mqjd.flink.jobs.chapter2.section3;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.mqjd.flink.env.Environment;
import org.mqjd.flink.env.EnvironmentParser;
import org.mqjd.flink.env.node.connector.JdbcConnectorNode;
import org.mqjd.flink.env.node.source.MySqlCdcNode;

public class CdcMySqlToMySql {

    private static final String JOB_YAML = "jobs/chapter2/section3/job.yaml";

    public static void main(String[] args) throws Exception {
        Environment environment = EnvironmentParser.parse(JOB_YAML, args);
        StreamExecutionEnvironment env =
            StreamExecutionEnvironment.getExecutionEnvironment(
                environment.getJobConfig().getConfiguration());
        TableEnvironment tableEnv =
            StreamTableEnvironment.create(env,
                EnvironmentSettings.newInstance().inStreamingMode().build());
        MySqlCdcNode source = environment.getSource();
        JdbcConnectorNode sink = environment.getSink();
        Schema schema = Schema.newBuilder()
            .column("id", DataTypes.INT().notNull())
            .column("user_id", DataTypes.STRING())
            .column("class_id", DataTypes.INT())
            .column("name", DataTypes.STRING())
            .column("age", DataTypes.INT())
            .column("create_time", DataTypes.TIMESTAMP(3))
            .column("create_by", DataTypes.STRING())
            .primaryKey("id")
            .build();

        tableEnv.createTable("source_table",
            TableDescriptor.forConnector("mysql-cdc")
                .schema(schema)
                .option("table-name", "user_source")
                .option("server-time-zone", "UTC")
                .option("scan.incremental.snapshot.chunk.key-column", "id")
                .option("hostname", source.getHostname())
                .option("port", String.valueOf(source.getPort()))
                .option("database-name", source.getDatabaseName())
                .option("username", source.getUsername())
                .option("password", source.getPassword())
                .build());
        tableEnv.createTable("target_table",
            TableDescriptor.forConnector("jdbc")
                .schema(schema)
                .option("table-name", "user_target")
                .option("url", sink.getUrl())
                .option("username", sink.getUsername())
                .option("password", sink.getPassword())
                .build());

        tableEnv.executeSql("insert into target_table select * from source_table");
    }

}
