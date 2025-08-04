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
                api(ktorLibs.http)
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
