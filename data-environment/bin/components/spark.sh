#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_master {
  init_standlone_conf
  init_standlone_dirs
}

function init_standlone_conf {
  mkdir_if_not_exists "$SPARK_CONF_DIR"
  if [ -z "$(ls -A "$SPARK_CONF_DIR")" ]; then
    cp -rf "$HD_HOME"/configs/spark/* "$SPARK_CONF_DIR"
  fi
}

function init_standlone_dirs {
  mkdir_if_not_exists "$SPARK_LOG_DIR"
}

function start_master {
  if pgrep -f "spark.*Master" >/dev/null; then
    echo "spark Master already exists"
  else
    start-master.sh
  fi
}

function install_worker {
  init_standlone_conf
  init_standlone_dirs
}

function start_worker {
  if pgrep -f "spark.*Worker" >/dev/null; then
    echo "spark Worker already exists"
  else
    start-worker.sh spark://hd1:7077
  fi
}

function init_client {
  init_standlone_conf
  init_standlone_dirs
}

function start_client {
  echo "spark client initialized"
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
