#!/usr/bin/env bash
# shellcheck disable=SC2034

set -eu

PWD=$(dirname "$0")
COMPONENT=$1
ARGS=("$@")
ACTIONS=("")
if [ ${#ARGS[@]} -gt 1 ]; then
  ACTIONS=("${ARGS[@]:1}")
fi

NETWORK_NAME="de_network"

# usql db
DB_PG="pg://mq:123456@172.19.0.2/mq"
DB_MYSQL="mysql://mq:123456@172.19.0.3/mq"

# docker-compose
CONTAINER_MYSQL="${PWD}"/docker/mysql/docker-compose.yml
CONTAINER_PG="${PWD}"/docker/pg/docker-compose.yml
CONTAINER_HD="${PWD}"/docker/hd/docker-compose.yml

function container {
  args=("$@")
  container_key=$(echo "container_${args[0]}" | tr "[:lower:]" "[:upper:]")
  container_path=$(eval "echo \$$container_key")
  args=("${args[@]:1}")
  docker-compose -f "${container_path}" "${args[@]}"
}

function exec {
  docker exec -it "$1" /bin/bash
}

function sql {
  args=("$@")
  db_key=$(echo "db_${args[0]}" | tr "[:lower:]" "[:upper:]")
  db_link=$(eval "echo \$$db_key")

  if [ ${#args[@]} -eq 1 ]; then
    usql "$db_link"
  elif [ ${#args[@]} -eq 3 ] && [ "${args[1]}" = "-d" ]; then
    find "${args[2]}" -type f -name "*.sql" | sort | xargs -I{} usql "$db_link" -f "{}"
  else
    new_args=("${args[@]:1}")
    usql "$db_link" "${new_args[@]}"
  fi
}

function hd {
  args=("$@")
  if [ "${args[0]}" = "build" ]; then
    bash "${PWD}"/docker/hd/image/build.sh
  elif ! docker image inspect hd:1.0 &>/dev/null; then
    echo "hd image has not build, run build first!"
  else
    container "hd" "${args[@]}"
  fi
}

function help {
  echo "Usage: ./de.sh [TYPE] [OPTIONS]"
  echo "TYPE:"
  echo "    container      exec docker-compose commands"
  echo "    exec           exec docker exec -it"
  echo "    sql            exec usql"
  echo "    hd             hadoop cluster"
}

function create_network {
  if ! docker network inspect $NETWORK_NAME &>/dev/null; then
      echo "Network $NETWORK_NAME does not exist. Creating..."
      docker network create \
          --driver bridge \
          --subnet=172.19.0.0/16 \
          --gateway=172.19.0.1 \
          --ip-range=172.19.0.0/24 \
          $NETWORK_NAME
      echo "Network $NETWORK_NAME created."
  fi
}

function main {
  create_network
  if declare -F "$COMPONENT" >/dev/null 2>&1; then
    $COMPONENT "${ACTIONS[@]}"
  else
    echo "no such method $COMPONENT"
  fi
}

main