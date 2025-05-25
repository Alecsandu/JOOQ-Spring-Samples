# JOOQ with SPRING samples

<hr/>

## Description

Samples that show different uses cases of jooq library with spring boot framework.

<hr/>

## Containerization

### Podman
```shell
podman build --network jooq-spring-samples_demo-net -t java-jooq-spring:0.0.1 -f Containerfile
```

### or Docker
```shell
docker build -t java-jooq-spring:0.0.1 .
```

If you want to remove the remaining layers run(this will remove all unused images):
```shell
podman image prune -f
```

### Create container with Podman

```shell
podman run -it --rm --network jooq-spring-samples_demo-net -p 8080:8080 java-jooq-spring:0.0.1
```

or with docker
```shell
docker run -it --rm --network jooq-spring-samples_demo-net -p 8080:8080 java-jooq-spring:0.0.1
```
