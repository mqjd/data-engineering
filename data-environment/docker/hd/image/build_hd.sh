#!/usr/bin/env bash
set -eu

echo

docker build -t "hd:1.0" - <<UserSpecificDocker
FROM hd-base:1.0

RUN apt-get -q update \
    && apt-get -q install -y --no-install-recommends\
      vim \
      net-tools \
      iputils-ping \
      dos2unix \
      net-tools \
      python3 \
      python3-pip \
      libcurl4  \
      libgssapi-krb5-2  \
      libldap-2.5-0  \
      libwrap0  \
      libsasl2-2  \
      libsasl2-modules  \
      libsasl2-modules-gssapi-mit  \
      openssl  \
      liblzma5  \
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
RUN usermod -s /bin/bash ${USER_NAME}
RUN echo "123456" > ${DOCKER_HOME_DIR}/hadoop-http-auth-signature-secret

RUN echo "source /opt/bigdata/bin/env.sh" >> /home/${USER_NAME}/.bashrc

UserSpecificDocker
