import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization)
    id("io.github.nomisrev")
    application
}

application.mainClass.set("MainKt")

dependencies {
    implementation("io.github.nomisrev:ktor-typed-api:0.0.1")
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    testImplementation(libs.kotlin.test)
    testImplementation(ktorLibs.server.testHost)
}

tasks.withType<KotlinCompile>().configureEach {
    incremental = false
}
