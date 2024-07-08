---
sidebar_position: 1
---

# 简介

在学习数据工程的过程中，我们常常需要使用到真实的环境。诸如Hadoop集群、Kafka集群等。对于数据开发人员而言，熟悉自己所学组件的部署过程过程也是十分重要的。  
生产环境中我们使用到的集群通常是分布式部署，而在本地搭建分布式环境既繁琐又容易出错，当前项目中使用Docker来进行集群环境搭建

## 组件支持情况

| 组件            | 支持 | 备注                                                                                           |
|---------------|----|----------------------------------------------------------------------------------------------|
| Hadoop        | ✅  | 当前组件：[namenode](http://hd1:50070), datanode, [resourcemanager](http://hd1:8088), nodemanager |
| Hive          | ✅  | 当前组件：metastore, hiveserver2                                                                  |
| Hbase         | ✅  | 当前组件：[master](http://hd1:16010/), regionserver                                               |
| Spark         | ✅  | Standalone: [master](http://hd1:8001), worker                                                |
| Flink         | ✅  | Standalone: [jobmanager](http://hd1:8081), taskmanager                                       |
| Kafka         | ✅  | 分布式                                                                                          |
| Zookeeper     | ✅  | 分布式                                                                                          |
| ClickHouse    | ✅  | 分布式                                                                                          |
| Airflow       | ✅  | Standalone: [webserver](http://hd1:7000), scheduler                                          |
| ElasticSearch | ✅  | 分布式: [elasticsearch](http://hd1:9200/)                                                       |
| MongoDB       | ✅  | 分布式sharding：configsvr, routersvr, shardsvr                                                   |
