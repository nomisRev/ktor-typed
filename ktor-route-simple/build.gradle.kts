plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("de.infix.testBalloon") version "0.3.1-K2.1.21"
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    api("io.ktor:ktor-server-netty:3.1.3")
    api("io.ktor:ktor-client-cio:3.1.3")
    api("io.ktor:ktor-client-content-negotiation:3.1.3")
    api("io.ktor:ktor-server-content-negotiation:3.1.3")
    api("io.ktor:ktor-serialization-kotlinx-json:3.1.3")
    implementation("io.ktor:ktor-server-auth-jwt:3.1.2")
    api("org.jetbrains.kotlin:kotlin-reflect:2.1.21")
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("io.ktor:ktor-server-sessions:3.1.3")
    implementation("io.ktor:ktor-server-core:3.1.3")

    testImplementation("de.infix.testBalloon:testBalloon-framework-core:0.3.1-K2.1.21")
//    testImplementation("io.ktor:ktor-server-test-host:3.1.3")
//    testImplementation("io.ktor:ktor-client-content-negotiation:3.1.3")
//    testImplementation("io.ktor:ktor-server-content-negotiation:3.1.3")
}
