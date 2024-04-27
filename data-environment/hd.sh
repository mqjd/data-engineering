#!/usr/bin/env bash
set -eu

pwd=$(dirname "$0")
action=$1
args=("$@")
components=("${args[@]:1}")

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

function main {
  for ((i = 0; i < ${#components[@]}; i++)); do
    component=${components[i]}
    func="${action}_${component}"
    if declare -F "$func" >/dev/null 2>&1; then
      $func
    else
      echo "no such action: ${action} ${component}"
    fi
  done
}

main
