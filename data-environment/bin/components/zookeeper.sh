#!/usr/bin/env bash

script_path=$(dirname "$0")
source $script_path/common.sh

host_name=$(hostname)
action=$1
args=("$@")
components=("${args[@]:1}")

function install_server {
  init_conf
  init_data
  init_log
}

function init_conf {
  mkdir_if_not_exists "${ZOO_CONF_DIR}"
  if [ -z "$(ls -A $ZOO_CONF_DIR)" ]; then
    cp -rf $ZOO_HOME/conf/* $ZOO_CONF_DIR/
    cp -rf $HD_HOME/configs/zookeeper/* $ZOO_CONF_DIR/
    rm -rf $ZOO_CONF_DIR/zoo_sample.cfg
  fi
}

function init_data {
  local ZOO_DATA_DIR=$(get_property_value "dataDir" "$ZOO_CONF_DIR/zoo.cfg")
  mkdir_if_not_exists $ZOO_DATA_DIR
  if [ -z "$(ls -A $ZOO_DATA_DIR)" ]; then
    echo "${host_name:2:1}" >>$ZOO_DATA_DIR/myid
  fi
}

function init_log {
  local ZOO_LOG_DIR=$(get_property_value "dataLogDir" "$ZOO_CONF_DIR/zoo.cfg")
  mkdir_if_not_exists $ZOO_LOG_DIR
}

function start_server {
  if ps -ef | grep -v grep | grep "QuorumPeerMain" >/dev/null; then
    echo "QuorumPeerMain already exists"
  else
    zkServer.sh --config $ZOO_CONF_DIR start
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
