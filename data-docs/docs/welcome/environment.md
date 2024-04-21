---
sidebar_position: 2
---

# 环境搭建

在学习数据工程的过程中，我们常常需要使用到真实的环境。诸如Hadoop集群、Kafka集群等。对于数据开发人员而言，熟悉自己所学组件的部署过程过程也是十分重要的。  
生产环境中我们使用到的集群通常是分布式部署，而在本地搭建分布式环境既繁琐又容易出错，当前项目中使用Docker来进行集群环境搭建

## 准备工作

## 组件支持情况

| 组件            | 支持 | 备注                                                 |
|---------------|----|----------------------------------------------------|
| Hadoop        | ✅  | 当前组件：namenode,datanode,resourcemanager,nodemanager |
| Hive          | ✅  | 当前组件：metastore,hiveserver2                         |
| Hbase         | ✅  | 当前组件：master,regionserver                           |
| Spark         | ✅  | Standalone:master,worker                           |
| Flink         | ✅  | Standalone:jobmanager,taskmanager                  |
| Kafka         | ✅  | 当前组件：broker                                        |
| Zookeeper     | ✅  | 当前组件：QuorumPeerMain                                |
| ClickHouse    | ✅  |                                                    |
| Airflow       | ✅  | Standalone                                         |
| ElasticSearch | ✅  | 分布式                                                |
| MongoDB       | ✅  | 当前组件：configsvr,routersvr,shardsvr                  |
