import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    kotlin("plugin.jpa") version "2.3.0"
    id("nu.studer.jooq") version "10.2"
    id("org.flywaydb.flyway") version "12.0.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    id("jacoco")
}

group = "com.wordiam"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("io.micrometer:micrometer-registry-prometheus")

    jooqGenerator("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("com.github.database-rider:rider-spring:1.44.0")
    testImplementation("org.junit.platform:junit-platform-suite-engine")
    testImplementation("org.flywaydb:flyway-core")
    testImplementation("io.mockk:mockk:1.14.9")
    testImplementation("com.ninja-squad:springmockk:5.0.1")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:2.0.3")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    finalizedBy(tasks.jacocoTestReport)
    useJUnitPlatform()

    // Ensure TestContainers can access Docker
    systemProperty("testcontainers.reuse.enable", "true")

    // Set test environment
    systemProperty("spring.profiles.active", "test")

    // Increase timeout for container startup
    systemProperty("junit.jupiter.execution.timeout.default", "300s")

    // Memory settings for tests
    jvmArgs("-Xmx2g", "-XX:+UseG1GC")
}

val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/openelifba"
val dbUser = System.getenv("DB_USER") ?: "openelifba"
val dbPassword = System.getenv("DB_PASSWORD") ?: "openelifba"

flyway {
    url = dbUrl
    user = dbUser
    password = dbPassword
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}

// jOOQ Configuration
jooq {
    version.set("3.18.14") // Align with Spring Boot 3.2.5's jOOQ version
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.TRACE
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = dbUrl
                    user = dbUser
                    password = dbPassword
                }
                generator.apply {
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        includes = ".*"
                        excludes = "flyway_schema_history"
                        inputSchema = "public"
                    }
                    target.apply {
                        packageName = "com.wordiam.openelifba.jooq.generated"
                        directory = "build/generated/source/jooq/main"
                    }
                    generate.apply {
                        isRecords = true
                        isPojos = true
                        isFluentSetters = true
                    }
                }
            }
        }
    }
}

tasks.named("build") {
    dependsOn("flywayMigrate")
}

tasks.named("generateJooq") {
    dependsOn("flywayMigrate")
}

detekt {
    toolVersion = "1.23.6"
    config.setFrom("$projectDir/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    autoCorrect = true
    source.setFrom("src/main/kotlin", "src/test/kotlin")
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
        md.required.set(true)
    }
}

ktlint {
    version.set("1.3.1")
    debug.set(false)
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.JSON)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}
