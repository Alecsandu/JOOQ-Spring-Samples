plugins {
    java
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jooq.jooq-codegen-gradle") version "3.19.29"
}

group = "com.dockerino"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val jooqVersion = "3.19.29"
val javaBase32Version = "1.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    /**
     * Observability stack
     * 1. Metrics & tracing endpoints - actuator
     * 2. Metrics registry with prometheus
     * 3. Tracing registry with zipkin
     */
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-zipkin")

    implementation("in.co.tasky:java-base32:$javaBase32Version")

    implementation("com.nimbusds:nimbus-jose-jwt")

    runtimeOnly("org.postgresql:postgresql")

    jooqCodegen("org.jooq:jooq-meta-extensions:$jooqVersion")
    jooqCodegen("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jooq {
    configuration {
        generator {
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"

                // https://www.jooq.org/doc/latest/manual/code-generation/codegen-meta-sources/codegen-ddl/
                properties {
                    property {
                        key = "scripts"
                        value = "src/main/resources/db/database.sql"
                    }
                    property {
                        key = "sort"
                        value = "semantic"
                    }
                    property {
                        key = "unqualifiedSchema"
                        value = "none"
                    }
                    property {
                        key = "defaultNameCase"
                        value = "lower"
                    }
                }

            }
            generate {
                withJooqVersionReference(true)
            }
            target {
                packageName = "com.dockerino.jooq.generated"
                directory = "${project.layout.buildDirectory.get()}/generated-sources/jooq/main"
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("${project.layout.buildDirectory.get()}/generated-sources/jooq/main")
        }
    }
}
