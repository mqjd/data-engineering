#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

host_name=$(hostname)
action=$1
args=("$@")
components=("${args[@]:1}")

function install_statestored {
  init_conf
}

function install_admissiond {
  init_conf
}

function install_catalogd {
  init_conf
}

function install_impalad {
  init_conf
}

function start_statestored {
  if pgrep -f "sbin/statestored" >/dev/null; then
    echo "statestored already exists"
  else
    impala.sh start statestored
  fi
}

function start_admissiond {
  if pgrep -f "sbin/admissiond" >/dev/null; then
    echo "statestored already exists"
  else
    impala.sh start admissiond
  fi
}

function start_catalogd {
  if pgrep -f "sbin/catalogd" >/dev/null; then
    echo "catalogd already exists"
  else
    impala.sh start catalogd
  fi
}

function start_impalad {
  if pgrep -f "sbin/impalad" >/dev/null; then
    echo "impalad already exists"
  else
    impala.sh start impalad
  fi
}

function init_conf {
  mkdir_if_not_exists "$IMPALA_LOG_DIR"
  mkdir_if_not_exists "$IMPALA_MINIDUMPS_DIR"
  mkdir_if_not_exists "${IMPALA_CONF_DIR}"
  if [ -z "$(ls -A "$IMPALA_CONF_DIR")" ]; then
    cp -rf "$IMPALA_HOME"/conf/* "$IMPALA_CONF_DIR"/
    cp -rf "$HD_HOME"/configs/impala/* "$IMPALA_CONF_DIR"/
    cp -f "$HD_HOME"/configs/hive/hive-site.xml "$IMPALA_CONF_DIR"/
    cp -f "$HD_HOME"/configs/hadoop/core-site.xml "$IMPALA_CONF_DIR"/
    cp -f "$HD_HOME"/configs/hadoop/hdfs-site.xml "$IMPALA_CONF_DIR"/
    sed -i "s/0\.0\.0\.0/${host_name}/g" "$IMPALA_CONF_DIR"/admissiond_flags
    sed -i "s/0\.0\.0\.0/${host_name}/g" "$IMPALA_CONF_DIR"/catalogd_flags
    sed -i "s/0\.0\.0\.0/${host_name}/g" "$IMPALA_CONF_DIR"/impalad_flags
    sed -i "s/0\.0\.0\.0/${host_name}/g" "$IMPALA_CONF_DIR"/statestored_flags
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
