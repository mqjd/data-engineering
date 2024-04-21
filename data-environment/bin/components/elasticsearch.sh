#!/usr/bin/env bash

script_path=$(dirname "$0")
source $script_path/common.sh

host_name=$(hostname)
action=$1
args=("$@")
components=("${args[@]:1}")

function install_server {
  init_conf
  init_log
}

function init_conf {
  mkdir_if_not_exists "${ES_CONF_DIR}"
  if [ -z "$(ls -A $ES_CONF_DIR)" ]; then
    cp -rf $ES_HOME/config/* $ES_CONF_DIR/
    cp -rf $HD_HOME/configs/es/* $ES_CONF_DIR/
    set_yml_property_value "node.name" "${host_name}" $ES_CONF_DIR/elasticsearch.yml
    set_yml_property_value "network.host" "${host_name}" $ES_CONF_DIR/elasticsearch.yml
  fi
}

function init_log {
  mkdir_if_not_exists $ES_LOG_DIR
  mkdir_if_not_exists $ES_DATA_DIR
}

function start_server {
  if ps -ef | grep -v grep | grep "Elasticsearch" >/dev/null; then
    echo "Elasticsearch already exists"
  else
    elasticsearch -d
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
