plugins {
    java
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jooq.jooq-codegen-gradle") version "3.19.23"
}

group = "com.dockerino"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
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

val jjwtVersion = "0.12.6"
val env: String? = System.getenv("BUILD_ENV")
val dbHost: String = if (env.equals("container")) "postgres" else "localhost"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-jooq")

    runtimeOnly("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    jooqCodegen("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jooq {
    configuration {
        generator {
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                includes = ".*"
                inputSchema = "public"
            }
            generate {
                jdbc {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://${dbHost}:5432/jooq-demo"
                    username = "postgres"
                    password = "root"
                }
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
