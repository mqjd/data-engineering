---
sidebar_position: 2
---

# hdfs

## WebUI

[HDFS](http://hd1:50070)默认端口**50070**

## 命令

### 概述

[hdfs 命令](https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/HDFSCommands.html)的基本使用方式如下：

### 基本操作

```shell
hdfs classpath [--glob |--jar <path> |-h |--help]
```

### version

```shell
hdfs version
```

### dfs

基本使用如下(类似[hadoop fs](intro))：
```shell
hdfs dfs [COMMAND [COMMAND_OPTIONS]]
```

### envvars

打印hadoop环境变量
```shell
hdfs envvars
```

### fetchdt

打印hadoop环境变量
```shell
hdfs fetchdt <opts> <token_file_path>
```



