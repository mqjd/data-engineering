#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_master {
  init_master_dirs
  init_conf
}

function install_tserver {
  init_tserver_dirs
  init_conf
}

function init_master_dirs {
  mkdir_if_not_exists "${KUDU_DATA_DIR}"/master/wal
  mkdir_if_not_exists "${KUDU_DATA_DIR}"/master/data
  mkdir_if_not_exists "${KUDU_LOG_DIR}"
}

function init_tserver_dirs {
  mkdir_if_not_exists "${KUDU_DATA_DIR}"/tserver/wal
  mkdir_if_not_exists "${KUDU_DATA_DIR}"/tserver/data
  mkdir_if_not_exists "${KUDU_LOG_DIR}"
}

function init_conf {
  mkdir_if_not_exists "${KUDU_CONF_DIR}"
  if [ -z "$(ls -A "$KUDU_CONF_DIR")" ]; then
    cp -rf "$HD_HOME"/configs/kudu/* "$KUDU_CONF_DIR"
  fi
}

function start_master {
  nohup kudu-master --unlock_unsafe_flags -time_source=system_unsync -flagfile="${KUDU_CONF_DIR}"/master.gflagfile >"$KUDU_LOG_DIR"/master.log 2>&1 &
}

function start_tserver {
  nohup kudu-tserver --unlock_unsafe_flags -time_source=system_unsync -flagfile="${KUDU_CONF_DIR}"/tserver.gflagfile >"$KUDU_LOG_DIR"/tserver.log 2>&1 &
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
