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
                api(libs.kotlinx.serialization)
                api(libs.kotlinx.coroutines)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
