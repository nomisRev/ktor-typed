plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.power-assert")
    id("org.jetbrains.kotlinx.kover")
}

dependencies {
    api(projects.typedApi)
    api(projects.parser)
}