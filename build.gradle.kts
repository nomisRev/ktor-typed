val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.power-assert") version "2.1.21"
    id("io.ktor.plugin") version "3.1.3"
    id("org.jetbrains.kotlinx.kover") version "0.7.5"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-call-logging:3.1.3")

    // Testing dependencies
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.1.3")
    testImplementation("io.ktor:ktor-client-content-negotiation:3.1.3")
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
}

tasks.test {
    useJUnitPlatform()
}

koverReport {
    defaults {
        html { onCheck = true }
    }

    // TODO remove ApplicationKt example, and move to docs / KotlinX Knit
    filters {
        excludes {
            // Exclude Application.kt from coverage as it's the entry point
            classes("com.example.ApplicationKt")
        }
    }

    verify {
        rule {
            minBound(70) // Minimum 70% line coverage
        }
    }
}
