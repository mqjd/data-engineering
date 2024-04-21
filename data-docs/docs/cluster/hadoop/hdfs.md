---
sidebar_position: 2
---

# Hdfs

## WebUI

[HDFS](http://hd1:50070)网页默认端口**50070**，参数配置如下：

```xml
<property>
  <name>dfs.namenode.http-address</name>
  <value>0.0.0.0:50070</value>
  <final>false</final>
  <source>hdfs-site.xml</source>
</property>
```

## 命令

### 概述

[hdfs 命令](https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html)的基本使用方式如下：

### 基本操作

```bash
hdfs classpath [--glob |--jar <path> |-h |--help]
```

#### version

```bash
hdfs version
```

#### dfs

基本使用如下(类似[hadoop fs](intro))：
```bash
hdfs dfs [COMMAND [COMMAND_OPTIONS]]
```

#### envvars

打印hadoop环境变量
```bash
hdfs envvars
```

#### fetchdt

从NameNode获取Token
```bash
hdfs fetchdt <opts> <token_file_path>
```

## 集群操作

可以增加参数[--daemon]在后台完成


### 启动

```bash
hdfs --daemon start namenode
hdfs --daemon start secondarynamenode
hdfs --daemon start datanode
hdfs --daemon start journalnode
hdfs --daemon start dfsrouter
```

### 停止

```bash
hdfs --daemon stop namenode
hdfs --daemon stop secondarynamenode
hdfs --daemon stop datanode
hdfs --daemon stop journalnode
hdfs --daemon stop dfsrouter
```


