# -------------------- Build the initial jar file -------------------- #
FROM gradle:8.12-jdk21-alpine AS builder
WORKDIR /opt/app
## Copy gradle build configs & settings ##
COPY build.gradle.kts .
COPY settings.gradle.kts .
RUN --mount=type=cache,target=/home/gradle/.gradle gradle dependencies --build-cache
COPY src ./src
RUN --mount=type=cache,target=/home/gradle/.gradle gradle build --build-cache
## Create custom jre using jdeps and jlink
RUN jar xf build/libs/demo-1.0.0.jar
RUN jdeps --ignore-missing-deps --print-module-deps -q --recursive --multi-release 21 --class-path 'BOOT-INF/lib/*' build/libs/demo-1.0.0.jar > deps.info
RUN jlink --add-modules $(cat deps.info) --strip-debug --compress 2 --no-header-files --no-man-pages --output /customjre


# -------------------- Extract layers -------------------- #
FROM eclipse-temurin:21-jre-alpine-3.21 AS extractor
WORKDIR /opt/app
COPY --from=builder /opt/app/build/libs/*.jar application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --launcher

# -------------------- Copy customJRE & layers -------------------- #
FROM alpine:3.21.3 AS final
ENV JAVA_HOME=/opt/java/jdk21
ENV PATH=$JAVA_HOME/bin:$PATH
COPY --from=builder /customjre $JAVA_HOME
WORKDIR /opt/app
COPY --from=extractor /opt/app/application/dependencies/ ./
COPY --from=extractor /opt/app/application/spring-boot-loader/ ./
COPY --from=extractor /opt/app/application/snapshot-dependencies/ ./
COPY --from=extractor /opt/app/application/application/ ./
RUN apk add --no-cache shadow coreutils
RUN groupadd -r app-group && useradd -r -g app-group app-user
RUN chown -R app-user:app-group /opt/app
USER app-user
ENTRYPOINT ["java", "-XX:+UseParallelGC", "org.springframework.boot.loader.launch.JarLauncher"]
