#!/usr/bin/env bash

script_path=$(dirname "$0")
source $script_path/common.sh

action=$1
args=("$@")
components=("${args[@]:1}")

function init_airflow {
  mkdir_if_not_exists $AIRFLOW_HOME
  AIRFLOW_VERSION=2.8.2
  PYTHON_VERSION="$(python3 --version | cut -d " " -f 2 | cut -d "." -f 1-2)"
  CONSTRAINT_URL="https://raw.githubusercontent.com/apache/airflow/constraints-${AIRFLOW_VERSION}/constraints-${PYTHON_VERSION}.txt"
  pip3 install "apache-airflow==${AIRFLOW_VERSION}" --constraint "${CONSTRAINT_URL}"
}

function start_airflow {
}


function main {
  for(( i=0;i<${#components[@]};i++)) 
  do
    component=${components[i]}
    func="${action}_${component}"
    $func
  done
}

main