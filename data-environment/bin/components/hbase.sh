#!/usr/bin/env bash

script_path=$(dirname "$0")
source $script_path/common.sh

host_name=$(hostname)
action=$1
args=("$@")
components=("${args[@]:1}")

function init_master {
  init_conf
  init_log
}

function init_regionserver {
  init_conf
  init_log
}

function init_conf {
  mkdir_if_not_exists "${HBASE_CONF_DIR}"
  if [ -z "$(ls -A $HBASE_CONF_DIR)" ]; then
    cp -rf $HBASE_HOME/conf/* $HBASE_CONF_DIR/
    cp -rf $HD_HOME/configs/hbase/* $HBASE_CONF_DIR/
    cp -rf $HADOOP_CONF_DIR/core-site.xml $HBASE_CONF_DIR/
    cp -rf $HADOOP_CONF_DIR/hdfs-site.xml $HBASE_CONF_DIR/
  fi
}

function init_log {
  mkdir_if_not_exists $HBASE_LOG_DIR
}

function start_master {
  hbase-daemon.sh start master
}

function start_regionserver {
  hbase-daemon.sh start regionserver
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