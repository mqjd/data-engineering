#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_master {
  init_conf
  init_log
}

function install_regionserver {
  init_conf
  init_log
}

function init_conf {
  mkdir_if_not_exists "${HBASE_CONF_DIR}"
  if [ -z "$(ls -A "$HBASE_CONF_DIR")" ]; then
    cp -rf "$HBASE_HOME"/conf/* "$HBASE_CONF_DIR"/
    cp -rf "$HD_HOME"/configs/hbase/* "$HBASE_CONF_DIR"/
    cp -rf "$HADOOP_CONF_DIR"/core-site.xml "$HBASE_CONF_DIR"/
    cp -rf "$HADOOP_CONF_DIR"/hdfs-site.xml "$HBASE_CONF_DIR"/
  fi
}

function init_log {
  mkdir_if_not_exists "$HBASE_LOG_DIR"
}

function start_master {
  if pgrep -f "HMaster" >/dev/null; then
    echo "HMaster already exists"
  else
    hbase-daemon.sh start master
  fi
}

function start_regionserver {
  if pgrep -f "HRegionServer" >/dev/null; then
    echo "HRegionServer already exists"
  else
    hbase-daemon.sh start regionserver
  fi
}

function main {
  for ((i = 0; i < ${#components[@]}; i++)); do
    component=${components[i]}
    func="${action}_${component}"
    if declare -F "$func" >/dev/null 2>&1; then
      $func
    fi
  done
}

main
