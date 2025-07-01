plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.power-assert")
    id("org.jetbrains.kotlinx.kover")
    alias(libs.plugins.ksp)
}

dependencies {
    api(projects.typedApi)
    api("io.ktor:ktor-server-core:3.1.3")
    api("io.ktor:ktor-client-core:3.1.3")
    api("io.ktor:ktor-serialization-kotlinx-json:3.1.3")

    kspTest(project(":typed-api-ksp"))
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.1.3")
    testImplementation("io.ktor:ktor-client-content-negotiation:3.1.3")
    testImplementation("io.ktor:ktor-server-content-negotiation:3.1.3")
    testImplementation("com.charleskorn.kaml:kaml:0.80.1")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.1")
    testImplementation("org.junit.platform:junit-platform-engine:1.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}