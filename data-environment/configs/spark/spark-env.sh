#!/usr/bin/env bash

# shellcheck disable=SC2155
export SPARK_DIST_CLASSPATH=$(hadoop --config "$HADOOP_CONF_DIR" classpath)
export SPARK_MASTER_HOST=hd1
export SPARK_MASTER_WEBUI_PORT=8001