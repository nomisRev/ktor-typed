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
}

tasks.withType<KotlinCompile>().configureEach {
    incremental = false
}
