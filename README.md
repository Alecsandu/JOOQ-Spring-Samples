# JOOQ with SPRING samples

## Description

Samples that show different uses cases of jooq library with spring boot framework.

## Containerization

### Docker
```shell
    docker build -t java-cool:v-noice .
```

### Podman
```shell
    podman build -t java-cool:v-noice .
```

If you want to remove the remaining layers run(this will remove all unused images):
```shell
    podman image prune -f
```

### Create container

```shell
    docker run -it --rm --network demo_demo-net -p 8080:8080 java-cool:v-noice
```
or with podman (network name can contain the root folder as a prefix)
```shell
    podman run -it --rm --network demo_demo-net -p 8080:8080 java-cool:v-noice
```