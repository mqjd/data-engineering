#!/usr/bin/env bash

function mkdirs_if_not_exists {
  local DIRS=("$@")
  for ((i = 0; i < ${#DIRS[@]}; i++)); do
    mkdir_if_not_exists $1
  done
}

function mkdir_if_not_exists {
  dir=$1
  if [[ ! -d "$dir" ]]; then
    mkdir -p $dir
  fi
}

function get_property_value {
  local property_name=$1
  local file=$2
  if [[ $file == *.xml ]]; then
    echo $(get_xml_kv_value "$property_name" "$file")
  elif [[ $file == *.cfg ]]; then
    echo $(get_properties_value "$property_name" "$file")
  elif [[ $file == *.properties ]]; then
    echo $(get_properties_value "$property_name" "$file")
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
  value=$(grep "^$property_name=" $file | cut -d'=' -f2)
  echo "$value"
}

function get_xml_kv_value {
  local property_name=$1
  local xml_file=$2
  value=$(grep -A1 "<name>$property_name</name>" "$xml_file" | grep '<value>' | sed -e 's/.*<value>\(.*\)<\/value>.*/\1/')
  echo "$value"
}
