---
sidebar_position: 8
---

# ClickHouse

## WebUI

暂无

## 安装

```bash
export LATEST_VERSION="24.2.2.71"

case $(uname -m) in
  x86_64) ARCH=amd64 ;;
  aarch64) ARCH=arm64 ;;
  *) echo "Unknown architecture $(uname -m)"; exit 1 ;;
esac

mkdir "clickhouse-$LATEST_VERSION"
cd "clickhouse-$LATEST_VERSION"

for PKG in clickhouse-common-static clickhouse-server clickhouse-client
do
  curl -fO "https://packages.clickhouse.com/tgz/stable/$PKG-$LATEST_VERSION-${ARCH}.tgz" \
    || curl -fO "https://packages.clickhouse.com/tgz/stable/$PKG-$LATEST_VERSION.tgz"
done


tar -xzvf "clickhouse-common-static-$LATEST_VERSION-${ARCH}.tgz" \
  || tar -xzvf "clickhouse-common-static-$LATEST_VERSION.tgz"
  
mv "clickhouse-common-static-$LATEST_VERSION" "clickhouse-common-static"
rm "clickhouse-common-static-$LATEST_VERSION.tgz"

tar -xzvf "clickhouse-server-$LATEST_VERSION-${ARCH}.tgz" \
  || tar -xzvf "clickhouse-server-$LATEST_VERSION.tgz"

mv "clickhouse-server-$LATEST_VERSION" "clickhouse-server"
rm "clickhouse-server-$LATEST_VERSION.tgz"

tar -xzvf "clickhouse-client-$LATEST_VERSION-${ARCH}.tgz" \
  || tar -xzvf "clickhouse-client-$LATEST_VERSION.tgz"

mv "clickhouse-client-$LATEST_VERSION" "clickhouse-client"
rm "clickhouse-client-$LATEST_VERSION.tgz"

```