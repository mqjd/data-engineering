#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_client {
  init_standlone_conf
  init_standlone_dirs
}

function install_jobmanager {
  init_standlone_conf
  init_standlone_dirs
}

function init_standlone_conf {
  mkdir_if_not_exists "$FLINK_CONF_DIR"
  if [ -z "$(ls -A "$FLINK_CONF_DIR")" ]; then
    cp -rf "$FLINK_HOME"/conf/* "$FLINK_CONF_DIR"
    cp -rf "$HD_HOME"/configs/flink/* "$FLINK_CONF_DIR"
  fi
}

function init_standlone_dirs {
  mkdir_if_not_exists "$FLINK_LOG_DIR"
}

function start_jobmanager {
  if pgrep -f "flink.*StandaloneSessionClusterEntrypoint" >/dev/null; then
    echo "flink JobManager already exists"
  else
    jobmanager.sh start
  fi
}

function install_taskmanager {
  init_standlone_conf
  init_standlone_dirs
}

function start_taskmanager {
  if pgrep -f "flink.*TaskManagerRunner" >/dev/null; then
    echo "flink TaskManager already exists"
  else
    taskmanager.sh start
  fi
}

function init_client {
  init_standlone_conf
  init_standlone_dirs
}

function start_client {
  echo "flink client initialized"
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
