#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")
host_name=$(hostname)

function install_server {
  init_conf
  init_log
  do_init_server
}

function do_init_server {
  export CLICKHOUSE_USER=mq
  sudo bash "${CH_HOME}/clickhouse-common-static/install/doinst.sh"
#  sudo "${CH_HOME}/clickhouse-common-static-dbg/install/doinst.sh"
  sudo bash "${CH_HOME}/clickhouse-server/install/doinst.sh"
}

function init_conf {
  mkdir_if_not_exists "${CH_CONF_DIR}"
  mkdir_if_not_exists "${CH_DATA_DIR}"
  if [ -z "$(ls -A "$CH_CONF_DIR")" ]; then
    cp -rf "${HD_HOME}"/configs/ch/* "${CH_CONF_DIR}/"
    sed -i "s/<shard>hd1<\/shard>/<shard>${host_name}<\/shard>/g; s/<replica>chd1<\/replica>/<replica>c${host_name}<\/replica>/g" "$CH_CONF_DIR"/server/config.xml
  fi
}

function init_log {
  mkdir_if_not_exists "${CH_LOG_DIR}"
}

function start_server {
  nohup clickhouse-server start --config-file="$CH_CONF_DIR"/server/config.xml > "$CH_LOG_DIR"/clickhouse.log 2>&1 &
}


function install_client {
  sudo "${CH_HOME}/clickhouse-client/install/doinst.sh"
}

function start_client {
  echo "no need to start"
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
