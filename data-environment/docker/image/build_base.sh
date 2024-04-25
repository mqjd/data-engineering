#!/usr/bin/env bash
set -eu

MIRROR="ubuntu";
if [ "$(uname -m)" = "arm64" ]; then
  MIRROR="ubuntu-ports"
fi

docker build -t "hd-base:1.0" - <<UserSpecificDocker
FROM ubuntu:22.04

RUN apt-get -q update && apt-get -q install -y ca-certificates

RUN echo "" > /etc/apt/sources.list
RUN echo "deb https://mirrors.aliyun.com/${MIRROR}/ jammy main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb https://mirrors.aliyun.com/${MIRROR}/ jammy-security main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb https://mirrors.aliyun.com/${MIRROR}/ jammy-updates main restricted universe multiverse" >> /etc/apt/sources.list
RUN echo "deb https://mirrors.aliyun.com/${MIRROR}/ jammy-backports main restricted universe multiverse" >> /etc/apt/sources.list

RUN apt-get -q update \
    && apt-get -q upgrade -y \
    && apt-get -q install -y --no-install-recommends\
      sudo \
      openssh-server \
      locales \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir /var/run/sshd
RUN locale-gen en_US.UTF-8
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'
ENV PYTHONIOENCODING=utf-8

CMD ["/usr/sbin/sshd", "-D"]

UserSpecificDocker