# MySQL CDC

## 下载依赖

下载[Debezium MySQL CDC Source Connector](https://www.confluent.io/hub/debezium/debezium-connector-mysql),解压到Kafka plugins目录

## 启动服务

启动 Zookeeper、Kafka、Kafka Connect、MySQL

## 初始化脚本

执行 mysql-cdc.sql

## 创建 Connect

### CDC => Topic

```shell
curl -X POST -H "Content-Type: application/json" \
  --data '{
  "name": "mysql-cdc-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "de",
    "database.password": "123456",
    "database.server.name": "mysql",
    "database.server.id": "184054",
    "topic.prefix": "mysql.cdc",
    "database.include.list": "de",
    "table.include.list": "de.user",
    "schema.history.internal.kafka.bootstrap.servers": "hd1:9092",
    "schema.history.internal.kafka.topic": "mysql.cdc.schema-changes",
    "include.schema.changes": "true",
    "tombstones.on.delete": "true",
    "transforms": "unwrap,route",
    "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
    "transforms.unwrap.drop.tombstones": "false",
    "transforms.unwrap.delete.handling.mode": "rewrite",
    "transforms.route.type": "org.apache.kafka.connect.transforms.RegexRouter",
    "transforms.route.regex": "mysql.cdc.de.(.*)",
    "transforms.route.replacement": "source_$1",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable": "true",
    "value.converter.schemas.enable": "true"
  }
}' \
http://hd1:8083/connectors
```

### Topic => DB

```shell
curl -X POST -H "Content-Type: application/json" \
  --data '{
  "name": "mysql-sink-connector",
  "config": {
    "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
    "connection.url": "jdbc:mysql://mysql:3306/de?user=de&password=123456",
    "topics": "source_user",
    "auto.create": "true",
    "auto.evolve": "true",
    "insert.mode": "upsert",
    "pk.fields": "id",
    "pk.mode": "record_key",
    "delete.enabled": "false",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable": "true",
    "value.converter.schemas.enable": "true",
    "transforms": "filterTombstones,renameTable,convertTS",
    "transforms.filterTombstones.type": "org.apache.kafka.connect.transforms.Filter",
    "transforms.filterTombstones.predicate": "isTombstone",
    "predicates": "isTombstone",
    "predicates.isTombstone.type": "org.apache.kafka.connect.transforms.predicates.RecordIsTombstone",
    "transforms.renameTable.type": "org.apache.kafka.connect.transforms.RegexRouter",
    "transforms.renameTable.regex": "source_(.*)",
    "transforms.renameTable.replacement": "target_$1",
    "transforms.convertTS.type": "org.apache.kafka.connect.transforms.TimestampConverter$Value",
    "transforms.convertTS.target.type": "string",
    "transforms.convertTS.field": "create_time",
    "transforms.convertTS.format": "yyyy-MM-dd HH:mm:ss",
    "transforms.convertTS.unix.precision": "milliseconds"
  }
}' \
http://hd1:8083/connectors
```

## 测试

### 插入数据

```sql
insert into user (user_id, class_id, name, age, create_by)
select *
from (WITH RECURSIVE data_generate AS (SELECT 0 AS num
                                       UNION ALL
                                       SELECT num + 1
                                       FROM data_generate
                                       WHERE num < 10 - 1)
      SELECT num + 1                                                   as user_id,
             FLOOR(RAND() * 18)                                        as class_id,
             concat('user', '_', FLOOR(num / 5) + 1, '_', num % 5 + 1) as name,
             FLOOR(RAND() * 40) + 10                                   as age,
             'admin'                                                   as create_by
      FROM data_generate) t;
```

### 删除数据

```sql
delete from user where id = 3;
```

### 修改数据

```sql
update user set name = 'xxx' where id = 4;
```

### 追加字段

```sql
alter table user add column `user_class` varchar(10) COMMENT '用户类别';
insert into user (user_id, class_id, user_class, name, age, create_by)
select *
from (WITH RECURSIVE data_generate AS (SELECT 0 AS num
                                       UNION ALL
                                       SELECT num + 1
                                       FROM data_generate
                                       WHERE num < 1 - 1)
      SELECT num + 1                                                   as user_id,
             FLOOR(RAND() * 18)                                        as class_id,
             concat('user', '_', FLOOR(num / 5) + 1, '_', num % 5 + 1) as user_class,
             concat('user', '_', FLOOR(num / 5) + 1, '_', num % 5 + 1) as name,
             FLOOR(RAND() * 40) + 10                                   as age,
             'admin'                                                   as create_by
      FROM data_generate) t;
```

### <font color="red">删除字段(不支持)</font>