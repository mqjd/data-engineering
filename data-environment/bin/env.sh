#!/usr/bin/env bash

export PATH=$PATH:$JAVA_HOME/bin
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
alias python=python3

if [[ -n $HADOOP_HOME ]]; then
  export PATH=$HADOOP_HOME/bin:$PATH
  export PATH=$HADOOP_HOME/sbin:$PATH
  export HADOOP_CONF_DIR=${HD_DATA_HOME}/configs/hadoop
  export HADOOP_LOG_DIR=${HD_DATA_HOME}/log/hadoop
fi

if [[ -n $SPARK_HOME ]]; then
  export PATH=$SPARK_HOME/bin:$PATH
  export PATH=$SPARK_HOME/sbin:$PATH
  export SPARK_CONF_DIR=${HD_DATA_HOME}/configs/spark
  export SPARK_LOG_DIR=${HD_DATA_HOME}/log/spark
fi

if [[ -n $ZOO_HOME ]]; then
  export PATH=$ZOO_HOME/bin:$PATH
  export ZOO_CONF_DIR=${HD_DATA_HOME}/configs/zookeeper
  export ZOO_LOG_DIR=${HD_DATA_HOME}/log/zookeeper
fi

if [[ -n $KAFKA_HOME ]]; then
  export PATH=$KAFKA_HOME/bin:$PATH
  export KAFKA_CONF_DIR=${HD_DATA_HOME}/configs/kafka
  export KAFKA_LOG_DIR=${HD_DATA_HOME}/log/kafka
fi

if [[ -n $HBASE_HOME ]]; then
  export PATH=$HBASE_HOME/bin:$PATH
  export HBASE_CONF_DIR=${HD_DATA_HOME}/configs/hbase
  export HBASE_LOG_DIR=${HD_DATA_HOME}/log/hbase
fi

if [[ -n $HIVE_HOME ]]; then
  export PATH=$HIVE_HOME/bin:$PATH
  export HIVE_CONF_DIR=${HD_DATA_HOME}/configs/hive
  export HIVE_LOG_DIR=${HD_DATA_HOME}/log/hive
fi

if [[ -n $FLINK_HOME ]]; then
  export PATH=$FLINK_HOME/bin:$PATH
  export FLINK_CONF_DIR=${HD_DATA_HOME}/configs/flink
  export FLINK_LOG_DIR=${HD_DATA_HOME}/log/flink
fi

if [[ -n $CH_HOME ]]; then
  export CH_CONF_DIR=${HD_DATA_HOME}/configs/ch
  export CH_LOG_DIR=${HD_DATA_HOME}/log/ch
  export CH_DATA_DIR=${HD_DATA_HOME}/data/ch
fi

if [[ -n $ES_HOME ]]; then
  export PATH=$ES_HOME/bin:$PATH
  export ES_CONF_DIR=${HD_DATA_HOME}/configs/es
  export ES_PATH_CONF=$ES_CONF_DIR
  export ES_LOG_DIR=${HD_DATA_HOME}/log/es
  export ES_DATA_DIR=${HD_DATA_HOME}/data/es
fi

if [[ -n $MONGODB_HOME ]]; then
  export PATH=$MONGODB_HOME/bin:$PATH
  export MONGODB_CONF_DIR=${HD_DATA_HOME}/configs/mongodb
  export MONGODB_LOG_DIR=${HD_DATA_HOME}/log/mongodb
  export MONGODB_DATA_DIR=${HD_DATA_HOME}/data/mongodb
fi

if [[ -n $MONGOSH_HOME ]]; then
  export PATH=$MONGOSH_HOME/bin:$PATH
fi
