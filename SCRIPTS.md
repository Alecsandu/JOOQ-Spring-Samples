
# Sample scripts for building docker image and creating docker container

Build image

```shell
    docker build -t java-cool:v-noice .
```
or with podman
```shell
    podman build -t java-cool:v-noice . && podman image prune -f
```

Create container

```shell
    docker run -it --rm --network demo_demo-net -p 8080:8080 java-cool:v-noice
```
or with podman (network name can contain the root folder as a prefix)
```shell
    podman run -it --rm --network demo_demo-net -p 8080:8080 java-cool:v-noice
```