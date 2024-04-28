#!/usr/bin/env bash
set -eu

pwd=$(dirname "$0")
action=$1
args=("$@")
components=("${args[@]:1}")

USER_NAME=${SUDO_USER:=$USER}
USER_ID=$(id -u "${USER_NAME}")
export USER_ID
export USER_NAME

function build_cluster {
  bash "${pwd}"/docker/image/build.sh
}

function start_cluster {
  docker-compose -f "${pwd}"/docker/hd/docker-compose.yml up -d
}

function stop_cluster {
  docker-compose -f "${pwd}"/docker/hd/docker-compose.yml down
}

function restart_cluster {
  stop_cluster
  start_cluster
}

function exec {
  docker exec -it "$1" /bin/bash
}

function main {
  for ((i = 0; i < ${#components[@]}; i++)); do
    component=${components[i]}
    func="${action}_${component}"
    if declare -F "$func" >/dev/null 2>&1; then
      $func
    elif declare -F "$action" >/dev/null 2>&1; then
      $action "$component"
    fi
  done
}

main
