#!/usr/bin/env bash

function mkdirs_if_not_exists {
  local DIRS=("$@") 
  for(( i=0;i<${#DIRS[@]};i++)) 
  do
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
  local xml_file=$2
  value=$(grep -A1 "<name>$property_name</name>" "$xml_file" | grep '<value>' | sed -e 's/.*<value>\(.*\)<\/value>.*/\1/')
  echo "$value"
}