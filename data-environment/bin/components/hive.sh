#!/usr/bin/env bash

script_path=$(dirname "$0")
source $script_path/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function init_metastore {
  init_conf
  init_log
}

function init_hiveserver2 {
  init_log
  init_conf
}

function init_conf {
  mkdir_if_not_exists "${HIVE_CONF_DIR}"
  if [ -z "$(ls -A $HIVE_CONF_DIR)" ]; then
    cp -rf $HIVE_HOME/conf/* $HIVE_CONF_DIR/
    cp -rf $HD_HOME/configs/hive/* $HIVE_CONF_DIR/
  fi
}

function init_log {
  mkdir_if_not_exists $HIVE_LOG_DIR
}

function start_metastore {
  if ps -ef | grep -v grep | grep "HiveMetaStore" > /dev/null
  then
    echo "HiveMetaStore already exists"
  else
    do_init_metastore
    nohup hive --service metastore > $HIVE_LOG_DIR/metastore.log 2>&1 &
  fi
}

function do_init_metastore {
  if hdfs dfs -ls /user/hive | grep "Found" > /dev/null
  then
    echo "metastore already initialized"
  else
    hdfs dfs -mkdir -p /user/hive/warehouse
    hdfs dfs -chown hive:hive /user/hive
    hdfs dfs -chown hive:hive /user/hive/warehouse
    hdfs dfs -mkdir -p /tmp
    hdfs dfs -chmod -R 777 /tmp
    schematool -dbType postgres -initSchema
  fi
}

function start_hiveserver2 {
  if ps -ef | grep -v grep | grep "HiveServer2" > /dev/null
  then
    echo "HiveServer2 already exists"
  else
    nohup hive --service hiveserver2 > $HIVE_LOG_DIR/hiveserver2.log 2>&1 &
  fi
}

function main {
  for(( i=0;i<${#components[@]};i++))
  do
    component=${components[i]}
    func="${action}_${component}"
    $func
  done
}

main