#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_metastore {
  init_conf
  init_log
}

function install_hiveserver2 {
  init_log
  init_conf
}

function init_conf {
  mkdir_if_not_exists "${HIVE_CONF_DIR}"
  if [ -z "$(ls -A "$HIVE_CONF_DIR")" ]; then
    cp -rf "$HIVE_HOME"/conf/* "$HIVE_CONF_DIR"/
    cp -rf "$HD_HOME"/configs/hive/* "$HIVE_CONF_DIR"/
  fi
}

function init_log {
  mkdir_if_not_exists "$HIVE_LOG_DIR"
}

function start_metastore {
  if pgrep -f "HiveMetaStore" >/dev/null; then
    echo "HiveMetaStore already exists"
  else
    do_init_metastore
    nohup hive --service metastore >"$HIVE_LOG_DIR"/metastore.log 2>&1 &
  fi
}

function do_init_metastore {
  if usql_static "pg://root:123456@postgres/postgres" -c "SELECT 1 FROM pg_user WHERE usename = 'hive'" | grep -q "1"; then
    echo "metastore already initialized"
  else
    promise_run usql_static "pg://root:123456@postgres/postgres" -f "$HD_HOME"/configs/hive/init-postgres.sql
    hdfs dfs -mkdir -p /user/hive/warehouse
    hdfs dfs -chown hive:hive /user/hive
    hdfs dfs -chown hive:hive /user/hive/warehouse
    hdfs dfs -mkdir -p /tmp
    hdfs dfs -chmod -R 777 /tmp
    schematool -dbType postgres -initSchema
  fi
}

function start_hiveserver2 {
  if pgrep -f "HiveServer2" >/dev/null; then
    echo "HiveServer2 already exists"
  else
    nohup hive --service hiveserver2 >"$HIVE_LOG_DIR"/hiveserver2.log 2>&1 &
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
