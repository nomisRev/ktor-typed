import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization)
    id("io.github.nomisrev")
}

dependencies {
    testImplementation("io.github.nomisrev:ktor-typed-api:0.0.1")
    testImplementation(ktorLibs.server.netty)
    testImplementation(ktorLibs.client.cio)
    testImplementation(ktorLibs.server.contentNegotiation)
    testImplementation(ktorLibs.client.contentNegotiation)
    testImplementation(ktorLibs.serialization.kotlinx.json)
    testImplementation(libs.kotlin.test)
    testImplementation(ktorLibs.server.testHost)
}

tasks.withType<KotlinCompile>().configureEach {
    incremental = false
}
