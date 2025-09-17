#!/usr/bin/env bash

sudo service ssh start
sudo sysctl -w vm.max_map_count=2000000
sudo swapoff -a
sudo ln -s /usr/bin/python3 /usr/bin/python

# shellcheck disable=SC2164
cd /opt/bigdata
find ./bin -type f -print0 | xargs -0 dos2unix
find ./configs -type f -print0 | xargs -0 dos2unix
bash bin/hd.sh >> run.log 2>&1 &
sleep infinity
