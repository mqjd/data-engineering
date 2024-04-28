package org.mqjd.flink.jobs.streamming.table;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class MySQL {

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.set(CoreOptions.DEFAULT_PARALLELISM, 1);
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment(configuration);
        environment.setParallelism(1);
        TableEnvironment tableEnv =
            StreamTableEnvironment.create(environment, EnvironmentSettings.newInstance().inStreamingMode().build());
        tableEnv.executeSql("""
            CREATE TABLE UserSourceTable (
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
              'hostname' = 'localhost',
              'port' = '3306',
              'table-name' = 'user',
              'database-name' = 'flink',
              'username' = 'flink',
              'password' = '123456'
            )
            """);
        tableEnv.executeSql("""
            CREATE TABLE UserTargetTable (
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
              'url' = 'jdbc:mysql://localhost:3306/flink',
              'table-name' = 'user1',
              'scan.fetch-size' = '2',
              'username' = 'flink',
              'password' = '123456'
            )""");
        tableEnv.executeSql(
            "insert into UserTargetTable select * from UserSourceTable where create_time > TIMESTAMP '2024-04-24 00:00:00'");
    }

}
