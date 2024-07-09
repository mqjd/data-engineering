#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_server {
  init_conf
  init_log
}

function init_conf {
  mkdir_if_not_exists "${SUPERSET_CONF_DIR}"
  if [ -z "$(ls -A "$SUPERSET_CONF_DIR")" ]; then
    cp -rf "${HD_HOME}"/configs/superset/* "${SUPERSET_CONF_DIR}/"
    tar xvzf "${SUPERSET_CONF_DIR}"/db.tar.gz -C "${SUPERSET_CONF_DIR}"
    rm -rf "${SUPERSET_CONF_DIR}"/db.tar.gz
  fi
}

function init_log {
  mkdir_if_not_exists "${SUPERSET_LOG_DIR}"
}

function init_server {
  superset db upgrade
  superset fab create-admin --username admin --firstname data --lastname hd --email "1378415278@qq.com" --password 123456
  superset load_examples
  superset init
}

function start_server {
  nohup superset run -h 0.0.0.0 -p 7100 --with-threads --reload >"$SUPERSET_LOG_DIR"/superset.log 2>&1 &
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
