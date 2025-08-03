import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization)
    id("io.github.nomisrev")
}

dependencies {
    testImplementation("io.github.nomisrev:spring-webflux-typed-api:0.0.1")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux:3.4.0")
    testImplementation(libs.kotlinx.coroutines.reactor)
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")

    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.0")
    testImplementation("io.projectreactor:reactor-test:3.7.1")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:3.4.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.1")
    testImplementation("org.junit.platform:junit-platform-engine:1.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}

tasks.withType<KotlinCompile>().configureEach {
    incremental = false
}
