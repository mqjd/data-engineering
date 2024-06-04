package org.mqjd.flink.jobs.chapter2.section3;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
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
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(
            environment.getJobConfig().getConfiguration());
        TableEnvironment tableEnv = StreamTableEnvironment.create(env,
            EnvironmentSettings.newInstance().inStreamingMode().build());
        MySqlCdcNode source = environment.getSource();
        JdbcConnectorNode sink = environment.getSink();

        tableEnv.executeSql(STR."""
            CREATE TABLE source_table (
              id int,
              user_id STRING,
              class_id int,
              name STRING,
              age int,
              create_time TIMESTAMP(3),
              create_by STRING,
              PRIMARY KEY (id) NOT ENFORCED
            ) WITH (
              'connector' = 'mysql-cdc',
              'scan.incremental.snapshot.chunk.key-column' = 'id',
              'table-name' = 'user_source',
              'hostname' = '\{source.getHostname()}',
              'port' = '\{source.getPort()}',
              'database-name' = '\{source.getDatabaseName()}',
              'username' = '\{source.getUsername()}',
              'password' = '\{source.getPassword()}'
            )
        """);

        tableEnv.executeSql(STR."""
            CREATE TABLE target_table (
              id int,
              user_id STRING,
              class_id int,
              name STRING,
              age int,
              create_time TIMESTAMP(3),
              create_by STRING,
              PRIMARY KEY (id) NOT ENFORCED
            ) WITH (
              'connector' = 'jdbc',
              'table-name' = 'user_target',
              'url' = '\{sink.getUrl()}',
              'username' = '\{sink.getUsername()}',
              'password' = '\{sink.getPassword()}'
            )
        """);
        tableEnv.executeSql("insert into target_table select * from source_table");
    }

}
