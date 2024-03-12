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

