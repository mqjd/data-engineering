#!/usr/bin/env bash

export HBASE_MANAGES_ZK=false
#export HBASE_MASTER_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=7000"
export HBASE_DISABLE_HADOOP_CLASSPATH_LOOKUP="true"