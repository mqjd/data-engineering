#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_standalone {
  init_conf
  init_log
  init_dags
}

function init_conf {
  mkdir_if_not_exists "${AIRFLOW_CONF_DIR}"
  if [ -z "$(ls -A "$AIRFLOW_CONF_DIR")" ]; then
    cp -rf "${HD_HOME}"/configs/airflow/* "${AIRFLOW_CONF_DIR}/"
  fi
}

function init_log {
  mkdir_if_not_exists "${AIRFLOW_LOG_DIR}"
}

function init_dags {
  mkdir_if_not_exists "${AIRFLOW_DATA_DIR}"
  mv "${AIRFLOW_CONF_DIR}"/dags "${AIRFLOW_DATA_DIR}"
  chmod 750 "${AIRFLOW_DATA_DIR}" -R
}

function start_standalone {
  nohup airflow standalone >"$AIRFLOW_LOG_DIR"/standalone.log 2>&1 &
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
