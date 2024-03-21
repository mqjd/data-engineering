#!/usr/bin/env bash

script_path=$(dirname "$0")
source $script_path/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function init_jobmanager {
  init_standlone_conf
  init_standlone_dirs
}

function init_standlone_conf {
  mkdir_if_not_exists $FLINK_CONF_DIR
  if [ -z "$(ls -A $FLINK_CONF_DIR)" ]; then
    cp -rf $FLINK_HOME/conf/* $FLINK_CONF_DIR
    cp -rf $HD_HOME/configs/flink/* $FLINK_CONF_DIR
  fi
}

function init_standlone_dirs {
  mkdir_if_not_exists $FLINK_LOG_DIR
}

function start_jobmanager {
  if ps -ef | grep -v grep | grep "flink" | grep "StandaloneSessionClusterEntrypoint" >/dev/null; then
    echo "flink JobManager already exists"
  else
    jobmanager.sh start
  fi
}

function init_taskmanager {
  init_standlone_conf
  init_standlone_dirs
}

function start_taskmanager {
  if ps -ef | grep -v grep | grep "flink" | grep "TaskManagerRunner" >/dev/null; then
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
    $func
  done
}

main
