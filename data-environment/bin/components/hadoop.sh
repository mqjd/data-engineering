#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_namenode {
  init_hadoop
  DFS_NAME_DIR=$(get_property_value "dfs.name.dir" "$HADOOP_CONF_DIR/hdfs-site.xml")
  mkdir_if_not_exists "$DFS_NAME_DIR"
  format_namenode
}

function format_namenode {
  DFS_NAME_DIR=$(get_property_value "dfs.name.dir" "$HADOOP_CONF_DIR/hdfs-site.xml")
  if [ -z "$(ls -A "$DFS_NAME_DIR")" ]; then
    hdfs namenode -format
  fi
}

function install_datanode {
  init_hadoop
  DFS_DATA_DIR=$(get_property_value "dfs.data.dir" "$HADOOP_CONF_DIR/hdfs-site.xml")
  mkdir_if_not_exists "$DFS_DATA_DIR"
}

function install_resourcemanager {
  init_hadoop
}

function install_nodemanager {
  init_hadoop
}

function init_hadoop {
  init_hadoop_conf
  init_hadoop_dirs
}

function init_hadoop_dirs {
  HADOOP_TMP_DIR=$(get_property_value "hadoop.tmp.dir" "$HADOOP_CONF_DIR/core-site.xml")
  DFS_TMP_DIR=$(get_property_value "dfs.tmp.dir" "$HADOOP_CONF_DIR/hdfs-site.xml")
  mkdir_if_not_exists "$HADOOP_TMP_DIR"
  mkdir_if_not_exists "$DFS_TMP_DIR"
  mkdir_if_not_exists "$HADOOP_LOG_DIR"
}

function init_hadoop_conf {
  mkdir_if_not_exists "$HADOOP_CONF_DIR"
  if [ -z "$(ls -A "$HADOOP_CONF_DIR")" ]; then
    cp -rf "$HADOOP_HOME"/etc/hadoop/* "$HADOOP_CONF_DIR"
    cp -rf "$HD_HOME"/configs/hadoop/* "$HADOOP_CONF_DIR"
  fi
}

function start_namenode {
  if  pgrep -f "hadoop.*NameNode" >/dev/null; then
    echo "NameNode already exists"
  else
    hdfs --daemon start namenode
  fi
}

function start_datanode {
  if pgrep -f "hadoop.*DataNode" >/dev/null; then
    echo "DataNode already exists"
  else
    hdfs --daemon start datanode
  fi
}

function start_resourcemanager {
  if pgrep -f "hadoop.*ResourceManager" >/dev/null; then
    echo "ResourceManager already exists"
  else
    yarn --daemon start resourcemanager
  fi
}

function start_nodemanager {
  if  pgrep -f "hadoop.*NodeManager" >/dev/null; then
    echo "NodeManager already exists"
  else
    yarn --daemon start nodemanager
  fi
}

function start_proxyserver {
  if  pgrep -f "hadoop.*ProxyServer" >/dev/null; then
    echo "ProxyServer already exists"
  else
    yarn --daemon start proxyserver
  fi
}

function start_historyserver {
  if  pgrep -f "hadoop.*ApplicationHistoryServer" >/dev/null; then
    echo "ApplicationHistoryServer already exists"
  else
    yarn --daemon start historyserver
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
