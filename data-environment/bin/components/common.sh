#!/usr/bin/env bash

function use_java17 {
  export JAVA_HOME=${JAVA17_HOME}
  export PATH=$JAVA_HOME/bin:$PATH
}

function use_java21 {
  export JAVA_HOME=${JAVA21_HOME}
  export PATH=$JAVA_HOME/bin:$PATH
}

function promise_run {
  while true; do
      "$@"
      if [ $? -eq 0 ]; then
          log_info "exec success."
          break
      else
          sleep 5s
      fi
  done
}

function log_round_run {
  start_time=$(date +"%Y-%m-%d %H:%M:%S")
  echo "$start_time [info] $* start" >> "${HD_HOME}"/run.log
  "$@" >> "${HD_HOME}"/run.log
  end_time=$(date +"%Y-%m-%d %H:%M:%S")
  echo "$end_time [info] $* end" >> "${HD_HOME}"/run.log
}

function log_run {
  current_time=$(date +"%Y-%m-%d %H:%M:%S")
  echo "$current_time [info] $*" >> "${HD_HOME}"/run.log
  "$@" >> "${HD_HOME}"/run.log
}

function log_info {
  current_time=$(date +"%Y-%m-%d %H:%M:%S")
  echo "$current_time [info] $*" >> "${HD_HOME}"/run.log
}

function log_warn {
  current_time=$(date +"%Y-%m-%d %H:%M:%S")
  echo "$current_time [warn] $*" >> "${HD_HOME}"/run.log
}

function log_error {
  current_time=$(date +"%Y-%m-%d %H:%M:%S")
  echo "$current_time [error] $*" >> "${HD_HOME}"/run.log
}

function mkdirs_if_not_exists {
  local DIRS=("$@")
  for ((i = 0; i < ${#DIRS[@]}; i++)); do
    mkdir_if_not_exists "$1"
  done
}

function mkdir_if_not_exists {
  dir=$1
  if [[ ! -d "$dir" ]]; then
    mkdir -p "$dir"
  fi
}

function get_property_value {
  local property_name=$1
  local file=$2
  if [[ $file == *.xml ]]; then
    value=$(get_xml_kv_value "$property_name" "$file")
    echo "$value" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'
  elif [[ $file == *.cfg ]]; then
    value=$(get_properties_value "$property_name" "$file")
    echo "$value" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'
  elif [[ $file == *.properties ]]; then
    value=$(get_properties_value "$property_name" "$file")
    echo "$value" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//'
  fi
}

function set_property_value {
  local key=$1
  local value=$2
  local file=$3
  sed -i "s/^\($key\s*=\s*\).*\$/\1$value/" $file
}

function set_yml_property_value {
  local key=$1
  local value=$2
  local file=$3

  if ! grep -q "^${key}:" "$file"; then
      echo "${key}: ${value}" >> "$file"
  else
      sed -i "s/^\($key\s*:\s*\).*\$/\1$value/" "$file"
  fi
}

function get_properties_value {
  local property_name=$1
  local file=$2
  value=$(grep "^$property_name=" "$file" | cut -d'=' -f2)
  echo "$value"
}

function get_xml_kv_value {
  local property_name=$1
  local xml_file=$2
  value=$(grep -A1 "<name>$property_name</name>" "$xml_file" | grep '<value>' | sed -e 's/.*<value>\(.*\)<\/value>.*/\1/')
  echo "$value"
}
