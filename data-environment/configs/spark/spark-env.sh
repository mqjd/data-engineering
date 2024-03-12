#!/usr/bin/env bash

export SPARK_DIST_CLASSPATH=$(hadoop --config $HADOOP_CONF_DIR classpath)
export SPARK_MASTER_HOST=hd1