#!/usr/bin/env bash

sudo service ssh start

# shellcheck disable=SC2164
cd /opt/bigdata
bash bin/hd.sh >> run.log 2>&1 &
sleep infinity
