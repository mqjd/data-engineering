#!/usr/bin/env bash

pwd=$(dirname "$0")
source "$pwd"/components/common.sh
source "$pwd"/env.sh

pwd=$(dirname "$0")
host_name=$(hostname)
serve_lines=()

while IFS= read -r line; do
    serve_lines+=("$line")
done < <(grep "$host_name:" "$pwd"/servers.yml)

function extract_substring {
  IFS="$2" read -ra arr <<<"$1"
  if [ "2" -eq "$#" ]; then
    len=${#arr[@]}
    echo "${arr[$len - 1]}"
  else
    echo "${arr[$3 - 1]}"
  fi
}

function start_server {
  server=$(extract_substring "$1" ":" 2)
  components_str=$(extract_substring "$1" ":" 3)
  action_str=$(extract_substring "$1" ":" 4)

  if [ "${action_str}" = "all" ]; then
    action_str="pre_install,install,start,post_install"
  fi

  IFS="," read -ra components <<< "$components_str"
  IFS="," read -ra actions <<< "$action_str"

  for ((j = 0; j < ${#actions[@]}; j++)); do
    log_run bash "${pwd}/components/${server}.sh" "${actions[j]}" "${components[@]}"
  done

}

function main {
  for ((i = 0; i < ${#serve_lines[@]}; i++)); do
    serve_line=${serve_lines[i]}
    if [[ $serve_line != \#* ]]; then
      log_round_run start_server "${serve_line}"
    fi
  done
}

main
