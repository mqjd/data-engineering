#!/usr/bin/env bash

script_path=$(dirname "$0")
source $script_path/common.sh

host_name=$(hostname)
action=$1
args=("$@")
components=("${args[@]:1}")

function init_broker {
  init_conf
  init_log
}

function init_conf {
  mkdir_if_not_exists "${KAFKA_CONF_DIR}"
  if [ -z "$(ls -A $KAFKA_CONF_DIR)" ]; then
    cp -rf $KAFKA_HOME/config/* $KAFKA_CONF_DIR/
    cp -rf $HD_HOME/configs/kafka/* $KAFKA_CONF_DIR/
    set_property_value "broker.id" "${host_name:2:1}" $KAFKA_CONF_DIR/server.properties
  fi
}

function init_log {
  local KAFKA_DATA_DIR=$(get_property_value "log.dirs" "$KAFKA_CONF_DIR/server.properties")
  mkdir_if_not_exists $KAFKA_DATA_DIR
  mkdir_if_not_exists $KAFKA_LOG_DIR
}

function start_broker {
  export KAFKA_LOG4J_OPTS="-Dlog4j.configuration=file:$KAFKA_CONF_DIR/log4j.properties"
  export KAFKA_HEAP_OPTS="-Xmx1G -Xms1G -XX:+UnlockExperimentalVMOptions"
  export LOG_DIR=$KAFKA_LOG_DIR
  kafka-server-start.sh -daemon $KAFKA_CONF_DIR/server.properties
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