plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.power-assert")
    id("org.jetbrains.kotlinx.kover")
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    api(project(":typed-api"))
    api(project(":parser"))
    implementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test)
}
