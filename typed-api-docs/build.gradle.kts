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
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation(libs.kotlin.test)
}
