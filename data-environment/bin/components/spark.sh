#!/usr/bin/env bash

script_path=$(dirname "$0")
source $script_path/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function init_master {
  init_standlone_conf
  init_standlone_dirs
}

function init_standlone_conf {
  mkdir_if_not_exists $SPARK_CONF_DIR
  if [ -z "$(ls -A $SPARK_CONF_DIR)" ]; then
    cp -rf $HD_HOME/configs/spark/* $SPARK_CONF_DIR
  fi
}

function init_standlone_dirs {
  mkdir_if_not_exists $SPARK_LOG_DIR
}

function start_master {
  start-master.sh
}

function init_worker {
  init_standlone_conf
  init_standlone_dirs
}

function start_worker {
  start-worker.sh spark://hd1:7077
}


function main {
  for(( i=0;i<${#components[@]};i++)) 
  do
    component=${components[i]}
    func="${action}_${component}"
    $func
  done
}

main

