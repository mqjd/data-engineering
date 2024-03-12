# 数据基础环境

HD为Hadoop的简写

## 说明

项目中需要用到诸如Hadoop、Spark等数据组件，涉及Standalone或者集群环境，在需要用到的环境中均通过Docker来进行构建

由于Mac中的Docker是基于虚拟机进行实现，所以在本机无法通过IP直连容器，此处通过[docker-connector](https://github.com/wenjunxiao/mac-docker-connector)进行宿主机与Docker网络进行桥接

## 基础镜像构建

### Base镜像构建

Base镜像基于linux/arm64 ubuntu:22.04进行构建，修改镜像源为阿里源，具体见[Ubuntu Ports镜像
](https://developer.aliyun.com/mirror/ubuntu-ports?spm=a2c6h.13651104.d-1008.9.7e5f4763adNP46)

```shell
docker build -t hd-base:1.0 ./docker/base
```

### HD基础镜像构建

HD镜像基于Base镜像添加数据项目常用的基础环境Python3、免密等

```shell
bash build.sh
```

## Docker网络桥接

1. 参考[官方文档](https://github.com/wenjunxiao/mac-docker-connector)完成安装和服务启动

2. 配置本地路由

```shell
docker_connector_config="$(brew --prefix)/etc/docker-connector.conf"
if ! grep -q "^route 172.18.0.0/16" $docker_connector_config; then
    echo "route 172.18.0.0/16" >> $docker_connector_config
fi
```

## 集群

### host配置

编辑 /etc/hosts 增加hosts文件中的内容

```shell
sudo vi /etc/hosts
```

### HD集群

#### 安装包准备

1. 请自行从组件官网下载需要的安装包，包括但不限于JDK、Hadoop、Spark等
2. 解压安装包到 **BASE_PACKAGE_PATH** (见[.env](./docker/hd/.env))
3. 根据解压后的目录名称修改[.env](./docker/hd/.env)文件中以**PACKAGE_DIR**结尾的变量

```shell

# 构建并启动HD
docker-compose -f ./docker/hd/docker-compose.yml up -d

# 停止HD并删除
docker-compose -f ./docker/hd/docker-compose.yml down
```




