import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("io.github.nomisrev.typedapi")
    application
}

application.mainClass.set("MainKt")

dependencies {
    implementation(ktorLibs.server.netty)
}

tasks.withType<KotlinCompile>().configureEach {
    incremental = false
}
