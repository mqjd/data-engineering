---
sidebar_position: 1
---

# Hadoop

## 命令

### 概述

[hadoop 命令](https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/FileSystemShell.html)的基本使用方式如下：

```bash
hadoop fs <args>
```

### 基本操作

- 创建文件夹
```bash
hadoop fs -mkdir /hd
```

- 本地文件上传hdfs
```bash
hadoop fs -put test.txt /hd
```

- 查看文件内容
```bash
hadoop fs -cat /hd/test.txt
```

- 查看文件夹
```bash
hadoop fs -ls /hd
```

- 复制文件到本地
```bash
hadoop fs -get /hd/test.txt ./1.txt
hadoop fs -get /hd/test.txt /tmp

```

- 删除文件或目录
```bash
hadoop fs -rm /hd/test.txt
hadoop fs -rm -r /hd
```
