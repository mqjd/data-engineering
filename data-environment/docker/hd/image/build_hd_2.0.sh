#!/usr/bin/env bash
set -eu

docker build -t "hd:2.0" --progress=plain - <<UserSpecificDocker
FROM hd:1.0

RUN apt-get -q update \
    && apt-get -q install -y --no-install-recommends\
      krb5-user \
      curl \
      g++ \
      language-pack-en \
      openjdk-8-jre-headless

#RUN apt-get clean && rm -rf /var/lib/apt/lists/*

UserSpecificDocker
