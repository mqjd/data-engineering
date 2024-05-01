#!/usr/bin/env bash
# shellcheck disable=SC2034

set -eu

CUR=$(dirname "$0")

USER_NAME=${SUDO_USER:=$USER}
USER_ID=$(id -u "${USER_NAME}")
export USER_ID
export USER_NAME

# Define the network name
NETWORK_NAME="de_network"

# Define database connection strings
DB_PG="pg://mq:123456@172.19.0.2/mq"
DB_MYSQL="mysql://mq:123456@172.19.0.3/mq"

# Define paths to docker-compose files
CONTAINER_MYSQL="${CUR}"/docker/mysql/docker-compose.yml
CONTAINER_PG="${CUR}"/docker/pg/docker-compose.yml
CONTAINER_HD="${CUR}"/docker/hd/docker-compose.yml

# Function to execute docker-compose commands
function container {
  args=("$@")
  container_key=$(echo "container_${args[0]}" | tr "[:lower:]" "[:upper:]")
  container_path=$(eval "echo \$$container_key")
  args=("${args[@]:1}")
  docker-compose -f "${container_path}" "${args[@]}"
}

# Function to execute docker exec
function exec {
  docker exec -it "$1" /bin/bash
}

# Function to execute usql commands
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

# Function to handle Hadoop cluster actions
function hd {
  args=("$@")
  if [ "${args[0]}" = "build" ]; then
    bash "${CUR}"/docker/hd/image/build.sh
  elif ! docker image inspect hd:1.0 &>/dev/null; then
    echo "hd image has not build, run build first!"
  else
    container "hd" "${args[@]}"
  fi
}

# Function to create network if it does not exist
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

print_usage() {
  echo "Usage: de -c | --container | -e | --exec | -s | --sql | -h | --hd | --help"
}

if [[ $# -eq 0 ]]; then
  print_usage
  exit 1
fi

case "$1" in
-c | --container)
  action="container"
  shift
  ;;
-e | --exec)
  action="exec"
  shift
  ;;
-s | --sql)
  action="sql"
  shift
  ;;
-hd)
  action="hd"
  shift
  ;;
-h | --help)
  print_usage
  exit 0
  ;;
*)
  echo "Error: Invalid argument"
  print_usage
  exit 1
  ;;
esac

$action "$@"
