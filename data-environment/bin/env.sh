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
