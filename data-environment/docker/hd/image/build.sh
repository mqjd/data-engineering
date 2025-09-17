#!/usr/bin/env bash
set -eu

pwd=$(dirname "$0")

DOCKER_HOME_DIR=${DOCKER_HOME_DIR:-/home/${USER_NAME}}

if [ "$(uname -s)" = "Darwin" ]; then
  GROUP_ID=1000
elif [ "$(uname -s)" = "Linux" ]; then
  GROUP_ID=$(id -g "${USER_NAME}")
else
  GROUP_ID=1000
fi

export GROUP_ID
export DOCKER_HOME_DIR

if grep -q "WSL" /proc/version; then
  sudo -E bash "${pwd}"/build_base.sh
  sudo -E bash "${pwd}"/build_hd.sh
  sudo -E bash "${pwd}"/build_hd_2.0.sh
else
  bash "${pwd}"/build_base.sh
  bash "${pwd}"/build_hd.sh
  bash "${pwd}"/build_hd_2.0.sh
fi