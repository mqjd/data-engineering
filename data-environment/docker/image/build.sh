#!/usr/bin/env bash
set -eu

pwd=$(dirname "$0")

bash "${pwd}"/build_base.sh
bash "${pwd}"/build_hd.sh