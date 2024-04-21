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
  action_str=$(extract_substring "$1" ":" 4)

  if [ "${action_str}" = "all" ]; then
    action_str="pre_install,install,start,post_install"
  fi

  IFS="," read -ra components <<< "$components_str"
  IFS="," read -ra actions <<< "$action_str"

  for ((j = 0; j < ${#actions[@]}; j++)); do
    echo "${server}" "${actions[j]}" "${components[@]}"
    bash "$script_path/components/${server}.sh" "${actions[j]}" "${components[@]}"
  done

}

function main {
  for ((i = 0; i < ${#serve_lines[@]}; i++)); do
    serve_line=${serve_lines[i]}
    if [[ $serve_line != \#* ]]; then
      start_serve "${serve_line}"
    fi
  done
}

main
