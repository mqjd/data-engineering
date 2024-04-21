#!/usr/bin/env bash

script_path=$(dirname "$0")
source $script_path/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_namenode {
  init_hadoop
  local DFS_NAME_DIR=$(get_property_value "dfs.name.dir" "$HADOOP_CONF_DIR/hdfs-site.xml")
  mkdir_if_not_exists $DFS_NAME_DIR
  format_namenode
}

function format_namenode {
  local DFS_NAME_DIR=$(get_property_value "dfs.name.dir" "$HADOOP_CONF_DIR/hdfs-site.xml")
  if [ -z "$(ls -A $DFS_NAME_DIR)" ]; then
    hdfs namenode -format
  fi
}

function install_datanode {
  init_hadoop
  local DFS_DATA_DIR=$(get_property_value "dfs.data.dir" "$HADOOP_CONF_DIR/hdfs-site.xml")
  mkdir_if_not_exists $DFS_DATA_DIR
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
  local HADOOP_TMP_DIR=$(get_property_value "hadoop.tmp.dir" "$HADOOP_CONF_DIR/core-site.xml")
  local DFS_TMP_DIR=$(get_property_value "dfs.tmp.dir" "$HADOOP_CONF_DIR/hdfs-site.xml")
  mkdir_if_not_exists $HADOOP_TMP_DIR
  mkdir_if_not_exists $DFS_TMP_DIR
  mkdir_if_not_exists $HADOOP_LOG_DIR
}

function init_hadoop_conf {
  mkdir_if_not_exists $HADOOP_CONF_DIR
  if [ -z "$(ls -A $HADOOP_CONF_DIR)" ]; then
    cp -rf $HADOOP_HOME/etc/hadoop/* $HADOOP_CONF_DIR
    cp -rf $HD_HOME/configs/hadoop/* $HADOOP_CONF_DIR
  fi
}

function start_namenode {
  if ps -ef | grep -v grep | grep "NameNode" >/dev/null; then
    echo "NameNode already exists"
  else
    hdfs --daemon start namenode
  fi
}

function start_datanode {
  if ps -ef | grep -v grep | grep "DataNode" >/dev/null; then
    echo "DataNode already exists"
  else
    hdfs --daemon start datanode
  fi
}

function start_resourcemanager {
  if ps -ef | grep -v grep | grep "ResourceManager" >/dev/null; then
    echo "ResourceManager already exists"
  else
    yarn --daemon start resourcemanager
  fi
}

function start_nodemanager {
  if ps -ef | grep -v grep | grep "NodeManager" >/dev/null; then
    echo "NodeManager already exists"
  else
    yarn --daemon start nodemanager
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
