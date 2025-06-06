# data-engineering
data-engineering

## build

```shell
mvn clean package -DskipTests -Dmvn.skip.test=true
```

## docker

### 查看容器IP

```shell
docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <容器名或ID>
```
