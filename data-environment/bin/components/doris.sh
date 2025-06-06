#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_fe {
  init_fe_conf
  init_fe_log
  init_fe_meta
}

function install_be {
  init_be_conf
  init_be_log
  init_be_storage
}

function install_follower {
  init_fe_conf
  init_fe_log
  init_fe_meta
}

function init_be_conf {
  ln -s -f "$HD_HOME"/configs/doris/be.palo_env.sh "$DORIS_HOME"/be/bin/palo_env.sh
  ln -s -f "$HD_HOME"/configs/doris/be_custom.conf "$DORIS_HOME"/be/conf/be_custom.conf
}

function init_fe_conf {
  ln -s -f "$HD_HOME"/configs/doris/fe_custom.conf "$DORIS_HOME"/fe/conf/fe_custom.conf
  ln -s -f "$HD_HOME"/configs/doris/fe.palo_env.sh "$DORIS_HOME"/fe/bin/palo_env.sh
}

function init_fe_meta {
  mkdir_if_not_exists "${DORIS_DATA_DIR}"/fe/meta_dir
  mkdir_if_not_exists "${DORIS_DATA_DIR}"/fe/temp_dir
}

function init_be_storage {
  mkdir_if_not_exists "${DORIS_DATA_DIR}"/be/storage
}

function init_fe_log {
  mkdir_if_not_exists "${DORIS_LOG_DIR}"/fe
}

function init_be_log {
  mkdir_if_not_exists "${DORIS_LOG_DIR}"/be
}

function start_fe {
  start_fe.sh --daemon
  promise_run usql_static "mysql://root:@hd1:9030" -f "$HD_HOME"/configs/doris/init.sql
}

function start_be {
  start_be.sh --daemon
}

function start_follower {
  start_fe.sh --helper hd1:9010 --daemon
}

function main {
  export JAVA_HOME=${JAVA17_HOME}
  ulimit -n 655350
  for ((i = 0; i < ${#components[@]}; i++)); do
    component=${components[i]}
    func="${action}_${component}"
    if declare -F "$func" >/dev/null 2>&1; then
      $func
    fi
  done
}

main
