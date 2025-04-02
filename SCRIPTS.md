
# Sample scripts for building docker images and running docker containers

```shell
docker build -t java-cool:v-noice .
```

```shell
docker run -it --rm --network demo_demo-net -p 8080:8080 java-cool:v-noice
```
