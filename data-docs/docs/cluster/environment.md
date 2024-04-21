---
sidebar_position: 2
---

# 环境搭建

## 准备工作

- 准备一台Mac电脑(当前脚本只在Mac下进行测试，后续可能会支持其他平台)
- 按照官方文档安装[Orbstack](https://docs.orbstack.dev/install)

## 镜像构建

环境构建相关脚本在项目**data-environment**目录

### Base镜像构建

Base镜像基于linux/arm64 ubuntu:22.04进行构建，修改镜像源为阿里源，具体见[Ubuntu Ports镜像
](https://developer.aliyun.com/mirror/ubuntu-ports?spm=a2c6h.13651104.d-1008.9.7e5f4763adNP46)

```bash
docker build -t hd-base:1.0 ./docker/base
```

### HD基础镜像构建

HD镜像基于Base镜像添加数据项目依赖的基础组件Python3、openssl、lib包等

```bash
bash build.sh
```

## 组件选择

打开**data-environment/bin/servers.yml**，根据自己需求取消行注释

## 启动集群

```bash
# 构建并启动HD
docker-compose -f ./docker/hd/docker-compose.yml up -d

# 停止HD并删除
docker-compose -f ./docker/hd/docker-compose.yml down
```

## Hosts修改

追加**data-environment/hosts**中的内容到本地hosts文件