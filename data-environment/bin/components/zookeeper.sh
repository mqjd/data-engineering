#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

host_name=$(hostname)
action=$1
args=("$@")
components=("${args[@]:1}")

function install_server {
  echo "ZOO_CONF_DIR: ${ZOO_CONF_DIR}" > ${HD_HOME}/run.log
  init_conf
  init_data
  init_log
}

function init_conf {
  log_run mkdir_if_not_exists "${ZOO_CONF_DIR}"
  if [ -z "$(ls -A "$ZOO_CONF_DIR")" ]; then
    log_run cp -rf "$ZOO_HOME"/conf/* "$ZOO_CONF_DIR"/
    log_run cp -rf "$HD_HOME"/configs/zookeeper/* "$ZOO_CONF_DIR"/
    log_run rm -rf "$ZOO_CONF_DIR"/zoo_sample.cfg
  fi
}

function init_data {
  ZOO_DATA_DIR=$(get_property_value "dataDir" "$ZOO_CONF_DIR/zoo.cfg")
  log_run mkdir_if_not_exists "$ZOO_DATA_DIR"
  if [ -z "$(ls -A "$ZOO_DATA_DIR")" ]; then
    echo "${host_name:2:1}" >>"$ZOO_DATA_DIR"/myid
  fi
}

function init_log {
  ZOO_LOG_DIR=$(get_property_value "dataLogDir" "$ZOO_CONF_DIR/zoo.cfg")
  log_run mkdir_if_not_exists "$ZOO_LOG_DIR"
}

function start_server {
  if pgrep -f "QuorumPeerMain" >/dev/null; then
    log_info "QuorumPeerMain already exists"
  else
    log_run zkServer.sh --config "$ZOO_CONF_DIR" start
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
