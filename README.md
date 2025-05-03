# JOOQ with SPRING samples

## Description

Samples that show different uses cases of jooq library with spring boot framework.

## Containerization

### Docker
```shell
docker build -t java-jooq-spring:0.0.1 .
```

### Podman
```shell
podman build -t java-jooq-spring:0.0.1 .
```

If you want to remove the remaining layers run(this will remove all unused images):
```shell
podman image prune -f
```

### Create container

```shell
docker run -it --rm --network jooq-spring-samples_demo-net -p 8080:8080 java-jooq-spring:0.0.1
```
or with podman (network name can contain the root folder as a prefix)
```shell
podman run -it --rm --network jooq-spring-samples_demo-net -p 8080:8080 java-jooq-spring:0.0.1
```