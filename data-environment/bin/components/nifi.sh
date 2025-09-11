#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function install_nifi {
  init_conf
  init_dirs
}

function init_conf {
  mkdir_if_not_exists "$NIFI_CONF_DIR"
  if [ -z "$(ls -A "$NIFI_CONF_DIR")" ]; then
    sed -i 's/BOOTSTRAP_CONF_DIR="${NIFI_HOME}\/conf"/BOOTSTRAP_CONF_DIR="${NIFI_CONF_DIR:-${NIFI_HOME}\/conf}"/g' "$HD_HOME"/nifi/bin/nifi.sh
    sed -i 's/-Dnifi.properties.file.path=${NIFI_HOME}\/conf/${BOOTSTRAP_CONF_DIR}/g' "$HD_HOME"/nifi/bin/nifi.sh
    cp -rf "$HD_HOME"/configs/nifi/* "$NIFI_CONF_DIR"
  fi
}

function init_dirs {
  mkdir_if_not_exists "${NIFI_DATA_DIR}"
  mkdir_if_not_exists "${NIFI_LOG_DIR}"
}

function start_nifi {
  if pgrep -f "NiFi" >/dev/null; then
    echo "nifi service already exists"
  else
    nohup nifi.sh run 2>&1 &
  fi
}

function main {
  use_java21
  for ((i = 0; i < ${#components[@]}; i++)); do
    component=${components[i]}
    func="${action}_${component}"
    if declare -F "$func" >/dev/null 2>&1; then
      $func
    fi
  done
}

main
