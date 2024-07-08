---
sidebar_position: 3
---

# Yarn

## WebUI

[ResourceManager](http://hd1:8088)网页默认端口**8088**，参数配置如下：
```xml
<property>
  <name>yarn.resourcemanager.webapp.address</name>
  <value>${yarn.resourcemanager.hostname}:8088</value>
  <final>false</final>
  <source>yarn-default.xml</source>
</property>
```

## 命令

### 概述

[yarn 命令](https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YarnCommands.html)的基本使用方式如下：

### 基本操作

```bash
yarn [SHELL_OPTIONS] COMMAND [GENERIC_OPTIONS] [SUB_COMMAND] [COMMAND_OPTIONS]
```

#### application

基本操作
```bash
yarn application [options] Usage: yarn app [options]
```

- 应用列表
```bash
yarn application -list
yarn application -list -appStates FINISHED
```

#### jar

基本操作
```bash
yarn jar <jar> [mainClass] args...
```

- 提交应用
```bash
yarn jar $HADOOP_HOME/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.3.6.jar pi 10 10
```

#### logs

基本操作
```bash
yarn logs -applicationId <application ID> [options]
```

- 查看日志
```bash
yarn logs -applicationId application_1713699747940_0001
```

#### node

基本操作
```bash
yarn node [options]
```

- 查看节点
```bash
yarn node -list
```

## 集群操作

可以增加参数[--daemon]在后台完成


### 启动

```bash
yarn --daemon start resourcemanager
yarn --daemon start nodemanager
yarn --daemon start proxyserver
yarn --daemon start historyserver
yarn --daemon start timelineserver
yarn --daemon start sharedcachemanager
yarn --daemon start registrydns
```

### 停止

```bash
yarn --daemon stop resourcemanager
yarn --daemon stop nodemanager
yarn --daemon stop proxyserver
yarn --daemon stop historyserver
yarn --daemon stop timelineserver
yarn --daemon stop sharedcachemanager
yarn --daemon stop registrydns
```