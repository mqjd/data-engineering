#!/usr/bin/env bash

script_path=$(dirname "$0")
source $script_path/env.sh

host_name=$(hostname)

serve_lines=($(grep "$host_name:" $script_path/servers.yml))

function init() {
  ./init/hadoop.sh
}

function extract_substring {
  IFS="$2" read -ra arr <<<"$1"
  if [ "2" -eq "$#" ]; then
    len=${#arr[@]}
    echo "${arr[$len - 1]}"
  else
    echo "${arr[$3 - 1]}"
  fi
}

function start_serve {
  server=$(extract_substring "$1" ":" 2)
  components_str=$(extract_substring "$1" ":" 3)
  IFS="," read -ra components <<<"$components_str"

  bash $script_path/components/${server}.sh init "${components[@]}"
  if [[ $1 != \#* ]]; then
    echo "${components[@]}"
    bash $script_path/components/${server}.sh start "${components[@]}"
  fi

}

function main {
  for ((i = 0; i < ${#serve_lines[@]}; i++)); do
    serve_line=${serve_lines[i]}
    start_serve $serve_line
  done
}

main
