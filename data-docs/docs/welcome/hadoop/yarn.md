---
sidebar_position: 3
---

# yarn

## WebUI

[ResourceManager](http://hd1:8088)默认端口**8088**，参数配置如下：
```xml
<property>
  <name>yarn.resourcemanager.webapp.address</name>
  <value>${yarn.resourcemanager.hostname}:8088</value>
  <final>false</final>
  <source>yarn-default.xml</source>
</property>
```

默认端口**8088**

## 命令

### 概述

[yarn 命令](https://hadoop.apache.org/docs/current/hadoop-yarn/hadoop-yarn-site/YarnCommands.html)的基本使用方式如下：

### 基本操作

```shell
yarn [SHELL_OPTIONS] COMMAND [GENERIC_OPTIONS] [SUB_COMMAND] [COMMAND_OPTIONS]
```

#### Application

- 应用列表
```shell
yarn application -list
yarn application -list -appStates FINISHED
```