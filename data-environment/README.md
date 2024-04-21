# 数据基础环境

HD为Hadoop的简写

## 说明

项目中需要用到诸如Hadoop、Spark等数据组件，涉及Standalone或者集群环境，在需要用到的环境中均通过Docker来进行构建

项目中用到的容器服务是[orbstack](https://orbstack.dev/),在开始之前请根据官网文档进行基础软件安装

## 基础镜像构建

### Base镜像构建

Base镜像基于linux/arm64 ubuntu:22.04进行构建，修改镜像源为阿里源，具体见[Ubuntu Ports镜像
](https://developer.aliyun.com/mirror/ubuntu-ports?spm=a2c6h.13651104.d-1008.9.7e5f4763adNP46)

```bash
docker build -t hd-base:1.0 ./docker/base
```

### HD基础镜像构建

HD镜像基于Base镜像添加数据项目常用的基础环境Python3、免密等

```bash
bash build.sh
```

## 集群

### host配置

编辑 /etc/hosts 增加hosts文件中的内容

```bash
sudo vi /etc/hosts
```

### HD集群

#### 安装包准备

1. 请自行从组件官网下载需要的安装包，包括但不限于JDK、Hadoop、Spark等
2. 解压安装包到 **BASE_PACKAGE_PATH** (见[.env](./docker/hd/.env))
3. 根据解压后的目录名称修改[.env](./docker/hd/.env)文件中以**PACKAGE_DIR**结尾的变量

```bash
# 构建并启动HD
docker-compose -f ./docker/hd/docker-compose.yml up -d

# 停止HD并删除
docker-compose -f ./docker/hd/docker-compose.yml down
```



