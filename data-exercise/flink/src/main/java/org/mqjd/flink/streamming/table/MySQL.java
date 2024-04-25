package org.mqjd.flink.streamming.table;

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
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment(
            configuration);
        TableEnvironment tableEnv = StreamTableEnvironment.create(environment,
            EnvironmentSettings.newInstance().inStreamingMode().build());
        tableEnv.executeSql(
            "CREATE TABLE UserSourceTable (\n" +
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
                "  'hostname' = 'localhost',\n" +
                "  'port' = '3306',\n" +
                "  'table-name' = 'user',\n" +
                "  'database-name' = 'flink',\n" +
                "  'username' = 'flink',\n" +
                "  'password' = '123456'\n" +
                ")");
        tableEnv.executeSql(
            "CREATE TABLE UserTargetTable (\n" +
                "  id int,\n" +
                "  user_id STRING,\n" +
                "  class_id int,\n" +
                "  name STRING,\n" +
                "  age int,\n" +
                "  create_time TIMESTAMP(3),\n" +
                "  create_by STRING,\n" +
                "  PRIMARY KEY (id) NOT ENFORCED\n" +
                ") WITH (\n" +
                "  'connector' = 'jdbc',\n" +
                "  'url' = 'jdbc:mysql://localhost:3306/flink',\n" +
                "  'table-name' = 'user1',\n" +
                "  'scan.fetch-size' = '2',\n" +
                "  'username' = 'flink',\n" +
                "  'password' = '123456'\n" +
                ")");
        tableEnv.executeSql(
            "insert into UserTargetTable select * from UserSourceTable where create_time > TIMESTAMP '2024-04-24 00:00:00'");
    }

}
