plugins {
    id("kotlin-multiplatform-common")
    alias(libs.plugins.serialization)
    alias(libs.plugins.power.assert)
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(11)
    sourceSets {
        commonMain {
            dependencies {
                api(project(":typed-api"))
                api(ktorLibs.server.core)
                api(ktorLibs.client.core)
                api(ktorLibs.serialization.kotlinx.json)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(ktorLibs.server.testHost)
                implementation(ktorLibs.server.contentNegotiation)
                implementation(ktorLibs.client.contentNegotiation)
                implementation(libs.kaml)
            }
        }
    }
}
