plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.power-assert")
    id("org.jetbrains.kotlinx.kover")
    // id("de.infix.testBalloon") version "0.3.1-K2.1.21"
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
//    api("io.ktor:ktor-server-netty:3.1.3")
//    api("io.ktor:ktor-client-cio:3.1.3")
//    api("io.ktor:ktor-client-content-negotiation:3.1.3")
//    api("io.ktor:ktor-server-content-negotiation:3.1.3")
    api("io.ktor:ktor-serialization-kotlinx-json:3.1.3")
//    api("org.jetbrains.kotlin:kotlin-reflect:2.1.21")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
//    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("io.ktor:ktor-server-cio:3.1.3")

    testImplementation(project(":ktor-yaml-converter"))
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:3.1.3")
    testImplementation("io.ktor:ktor-client-content-negotiation:3.1.3")
    testImplementation("io.ktor:ktor-server-content-negotiation:3.1.3")
    testImplementation("com.charleskorn.kaml:kaml:0.80.1")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.1")
    testImplementation("org.junit.platform:junit-platform-engine:1.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}