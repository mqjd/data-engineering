#!/usr/bin/env bash

sudo service ssh start

cd /opt/bigdata
bash bin/hd.sh > run.log 2>&1 &
sleep infinity