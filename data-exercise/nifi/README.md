# NIFI

## MySQL CDC Demo

### 组件

- NIFI
- Kafka
- Zookeeper
- MySQL

### 步骤

1. 准备MySQL表及数据
在de数据库执行[mysql.sql](mysql.sql) 脚本
2. 创建kafka connect
    ```shell
    curl -X POST -H "Content-Type: application/json" \
      --data '{
        "name": "nifi-mysql-cdc-connector",
        "config": {
          "connector.class": "io.debezium.connector.mysql.MySqlConnector",
          "tasks.max": "1",
          "database.hostname": "mysql",
          "database.port": "3306",
          "database.user": "de",
          "database.password": "123456",
          "database.server.id": "184054",
          "topic.prefix": "mysql.cdc",
          "database.include.list": "de", 
          "table.include.list": "de.user",
          "schema.history.internal.kafka.bootstrap.servers": "hd1:9092",
          "schema.history.internal.kafka.topic": "schema-changes.nifi-cdc-server",
          "include.schema.changes": "true"
        }
      }' \
      http://hd1:8083/connectors
    ```
3. 导入准备好的NIFI配置
新建 Process Group => 点击导入 => 选择 [MySQL_CDC.json](MySQL_CDC.json)
4. 启动NIFI Flow
5. 查看输出表(user_out)

