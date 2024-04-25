---
sidebar_position: 3
---

# Kafka

## WebUI

暂无

## 命令

### Topic

#### create

```bash
kafka-topics.sh --bootstrap-server hd1:9092 --create --topic hd-test-topic --partitions 2
```

#### alter

```bash
kafka-topics.sh --bootstrap-server hd1:9092 --alter --topic hd-test-topic --partitions 3
```

#### describe

```bash
kafka-topics.sh --bootstrap-server hd1:9092 --describe --topic hd-test-topic
```

#### delete

```bash
kafka-topics.sh --bootstrap-server hd1:9092 --delete --topic hd-test-topic
```

#### list

```bash
kafka-topics.sh --bootstrap-server hd1:9092 --list
```

### 消息

#### 基础

##### 生产

- 生产无key消息

```bash
kafka-console-producer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --producer.config ${KAFKA_CONF_DIR}/producer.properties
```

- 生产有key消息
```bash
kafka-console-producer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --producer.config ${KAFKA_CONF_DIR}/producer.properties --property parse.key=true
```

##### 消费

- 从开始位置消费

```bash
kafka-console-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --from-beginning
```

- 显示key

```bash
kafka-console-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --from-beginning --property parse.key=true
```

- 指定分区和偏移

```bash
kafka-console-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --partition 0 --offset 100 --from-beginning --property parse.key=true
```

- 设置消费组

```bash
kafka-console-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --group hd-test-group
```

- 使用consumer配置文件

```bash
kafka-console-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --consumer.config ${KAFKA_CONF_DIR}/consumer.properties
```

#### 批量

##### 生产

**--max-messages**：总条数，**--throughput**：吞吐(条/秒)

```bash
kafka-verifiable-producer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --max-messages 10 --throughput 1
```

##### 消费

```bash
kafka-verifiable-consumer.sh --bootstrap-server hd1:9092 --topic hd-test-topic --group-id hd-test-group --max-messages 10
```


#### 压测


##### 生产

**--max-messages**：总条数，**--throughput**：吞吐(条/秒)

```bash
for ((i=1; i<=100; i++))
do
    echo "这是第$i行数据" >> /opt/bigdata/kafka-payload-file.txt
done

kafka-producer-perf-test.sh --topic hd-test-topic --num-records 100 --throughput 100 --producer-props bootstrap.servers=hd1:9092 --payload-file /opt/bigdata/kafka-payload-file.txt
```

##### 消费

```bash
kafka-consumer-perf-test.sh --bootstrap-server hd1:9092 --topic hd-test-topic --group hd-test-group --messages 100
```

## 集群操作

### 启动

```bash
kafka-server-start.sh -daemon $KAFKA_CONF_DIR/server.properties
```

### 停止

```bash
kafka-server-stop.sh
```