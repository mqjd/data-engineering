#!/usr/bin/env bash
set -eu

USER_NAME=${SUDO_USER:=$USER}
USER_ID=$(id -u "${USER_NAME}")

DOCKER_HOME_DIR=${DOCKER_HOME_DIR:-/home/${USER_NAME}}

if [ "$(uname -s)" = "Darwin" ]; then
  GROUP_ID=100
fi

if [ "$(uname -s)" = "Linux" ]; then
  GROUP_ID=$(id -g "${USER_NAME}")
fi

docker build -t "hd:1.0" - <<UserSpecificDocker
FROM hd-base:1.0

RUN apt-get -q update \
    && apt-get -q install -y --no-install-recommends\
      python3 \
      python3-pip \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin without-password/' /etc/ssh/sshd_config
RUN sed -i 's/#PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config

RUN groupadd --non-unique -g ${GROUP_ID} ${USER_NAME}
RUN useradd -g ${GROUP_ID} -u ${USER_ID} -k /root -m ${USER_NAME} -d "${DOCKER_HOME_DIR}"
RUN echo "${USER_NAME} ALL=NOPASSWD: ALL" > "/etc/sudoers.d/${USER_NAME}"
ENV HOME "${DOCKER_HOME_DIR}"

RUN mkdir -p /opt/bigdata
RUN mkdir -p /var/bigdata

RUN chown ${USER_NAME}:${GROUP_ID} /opt/bigdata -R
RUN chown ${USER_NAME}:${GROUP_ID} /var/bigdata -R

RUN mkdir -p ${DOCKER_HOME_DIR}/.ssh
RUN ssh-keygen -t rsa -b 4096 -C "${USER_NAME}" -N '' -f ${DOCKER_HOME_DIR}/.ssh/id_rsa
RUN cat ${DOCKER_HOME_DIR}/.ssh/id_rsa.pub > ${DOCKER_HOME_DIR}/.ssh/authorized_keys
RUN chown ${USER_NAME}:${GROUP_ID} ${DOCKER_HOME_DIR}/.ssh/ -R
RUN chown -R ${USER_NAME}:${GROUP_ID} /etc/ssh

RUN echo "123456" > ${DOCKER_HOME_DIR}/hadoop-http-auth-signature-secret

RUN echo "source /opt/bigdata/bin/env.sh" >> /home/${USER_NAME}/.bashrc

UserSpecificDocker
