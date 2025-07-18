plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization)
    alias(libs.plugins.power.assert)
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":typed-api"))
    api("org.springframework.boot:spring-boot-starter-webflux:3.4.0")
    api(libs.kotlinx.coroutines.reactor)
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")

    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.4.0")
    testImplementation("io.projectreactor:reactor-test:3.7.1")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:3.4.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.1")
    testImplementation("org.junit.platform:junit-platform-engine:1.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}