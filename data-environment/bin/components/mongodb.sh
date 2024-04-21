#!/usr/bin/env bash

script_path=$(dirname "$0")
source "$script_path"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_configsvr {
  init_conf
  init_log
  mkdir_if_not_exists "$MONGODB_DATA_DIR/configsvr"
}

function install_routersvr {
  init_conf
  init_log
  mkdir_if_not_exists "$MONGODB_DATA_DIR/routersvr"
}

function install_shardsvr {
  init_conf
  init_log
  mkdir_if_not_exists "$MONGODB_DATA_DIR/shardsvr"
}

function init_conf {
  mkdir_if_not_exists "${MONGODB_CONF_DIR}"
  if [ -z "$(ls -A "$MONGODB_CONF_DIR")" ]; then
    cp -rf "$HD_HOME"/configs/mongodb/* "$MONGODB_CONF_DIR"/
  fi
}

function init_log {
  mkdir_if_not_exists "$MONGODB_LOG_DIR"
  mkdir_if_not_exists "$MONGODB_DATA_DIR"
}

function start_configsvr {
  if ps -ef | grep -v grep | grep "configsvr.yml" >/dev/null; then
    echo "configsvr already exists"
  else
    mongod -f "$MONGODB_CONF_DIR/configsvr.yml"
  fi
}

function start_routersvr {
  if ps -ef | grep -v grep | grep "routersvr.yml" >/dev/null; then
    echo "routersvr already exists"
  else
    mongos --config "$MONGODB_CONF_DIR/routersvr.yml"
  fi
}

function start_shardsvr {
  if ps -ef | grep -v grep | grep "shardsvr.yml" >/dev/null; then
    echo "shardsvr already exists"
  else
    mongod -f "$MONGODB_CONF_DIR/shardsvr.yml"
  fi
}

function post_install_configsvr {
  mongosh --port 27019 --file "$MONGODB_CONF_DIR/configsvr_init.js"
}

function post_install_shardsvr {
  mongosh --port 27018 --file "$MONGODB_CONF_DIR/shardsvr_init.js"
}

function post_install_routersvr {
  mongosh --port 27017 --file "$MONGODB_CONF_DIR/routersvr_init.js"
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
