---
sidebar_position: 4
---

# Flink

## WebUI

[JobManager](http://hd1:8081)网页默认端口**8081**，参数配置如下：

```yaml
rest.port: 8081
```

## 命令

[官方说明](https://nightlies.apache.org/flink/flink-docs-master/docs/deployment/cli/)

### run

提交Flink作业。部分参数说明如下：

| 参数                      | 说明                                                                                                                                                                                     |
|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| -c,--class \<classname> | 指定带有main函数的class入口类                                                                                                                                                                    |
| -d,--detached           | 分离模式                                                                                                                                                                                   |
| -t,--target \<arg>      | For the "run" action: "remote", "local", "kubernetes-session", "yarn-per-job" (deprecated), "yarn-session".For "run-application" action:   "kubernetes-application","yarn-application" |
| -D \<property=value>    | 指定flink运行参数                                                                                                                                                                            |

使用示例：

```shell
flink run -e local $FLINK_HOME/examples/streaming/WordCount.jar
flink run-application -d -t yarn-application $FLINK_HOME/examples/streaming/WordCount.jar
```

### run-application

### info

### list

### stop

### cancel

### savepoint

### checkpoint
