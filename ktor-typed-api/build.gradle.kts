plugins {
    id("kotlin-multiplatform-common")
    kotlin("plugin.serialization")
    kotlin("plugin.power-assert")
    id("org.jetbrains.kotlinx.kover")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":typed-api"))
                api("io.ktor:ktor-server-core:3.1.3")
                api("io.ktor:ktor-client-core:3.1.3")
                api("io.ktor:ktor-serialization-kotlinx-json:3.1.3")
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation("io.ktor:ktor-server-test-host:3.1.3")
                implementation("io.ktor:ktor-client-content-negotiation:3.1.3")
                implementation("io.ktor:ktor-server-content-negotiation:3.1.3")
                implementation("com.charleskorn.kaml:kaml:0.80.1")
            }
        }
    }
}
