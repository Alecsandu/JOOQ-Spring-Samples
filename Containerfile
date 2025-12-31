# -------------------- Build the initial jar file -------------------- #
FROM gradle:9.2.1-jdk25-alpine AS builder

## Copy gradle build configs & settings ##
WORKDIR /opt/app
COPY build.gradle.kts .
COPY settings.gradle.kts .

## ENV BUILD_ENV="container"  ## needed only if jooq is configured to generate by connecting to the db and using the db schema

RUN --mount=type=cache,target=/home/gradle/.gradle gradle dependencies --build-cache --no-daemon
COPY src ./src
RUN --mount=type=cache,target=/home/gradle/.gradle gradle jooqCodegen --build-cache --no-daemon
RUN --mount=type=cache,target=/home/gradle/.gradle gradle build --build-cache --no-daemon

## Create custom jre using jdeps and jlink
RUN jar xf build/libs/jooq-spring-samples-1.0.0.jar
RUN jdeps --ignore-missing-deps --print-module-deps -q --recursive --multi-release 21 --class-path 'BOOT-INF/lib/*' build/libs/jooq-spring-samples-1.0.0.jar > deps.info
RUN jlink --add-modules $(cat deps.info) --strip-debug --compress 2 --no-header-files --no-man-pages --output /customjre


# -------------------- Extract layers -------------------- #
FROM eclipse-temurin:25-jre-alpine-3.23 AS extractor

WORKDIR /opt/app
COPY --from=builder /opt/app/build/libs/*.jar application.jar

RUN java -Djarmode=tools -jar application.jar extract --layers --launcher


# -------------------- Copy customJRE & layers -------------------- #
FROM alpine:3.23.2 AS final

ENV JAVA_HOME=/opt/java/jdk21
ENV PATH=$JAVA_HOME/bin:$PATH

COPY --from=builder /customjre $JAVA_HOME

WORKDIR /opt/app
COPY --from=extractor /opt/app/application/dependencies/ ./
COPY --from=extractor /opt/app/application/spring-boot-loader/ ./
COPY --from=extractor /opt/app/application/snapshot-dependencies/ ./
COPY --from=extractor /opt/app/application/application/ ./

RUN addgroup -S app-group \
    && adduser -S app-user -G app-group \
    && chown -R app-user:app-group /opt/app

USER app-user

ENTRYPOINT ["java", "-XX:+UseParallelGC", "-Dspring.profiles.active=docker", "org.springframework.boot.loader.launch.JarLauncher"]
