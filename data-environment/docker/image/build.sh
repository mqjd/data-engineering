#!/usr/bin/env bash
set -eu

pwd=$(dirname "$0")

USER_NAME=${SUDO_USER:=$USER}
USER_ID=$(id -u "${USER_NAME}")
DOCKER_HOME_DIR=${DOCKER_HOME_DIR:-/home/${USER_NAME}}

if [ "$(uname -s)" = "Darwin" ]; then
  GROUP_ID=1000
elif [ "$(uname -s)" = "Linux" ]; then
  GROUP_ID=$(id -g "${USER_NAME}")
else
  GROUP_ID=1000
fi

export USER_ID=$USER_ID
export USER_NAME
export GROUP_ID
export DOCKER_HOME_DIR

if grep -q "WSL" /proc/version; then
  sudo -E bash "${pwd}"/build_base.sh
  sudo -E bash "${pwd}"/build_hd.sh
else
  bash "${pwd}"/build_base.sh
  bash "${pwd}"/build_hd.sh
fi