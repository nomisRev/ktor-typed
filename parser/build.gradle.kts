plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.buildconfig)
}

kotlin {
    explicitApi()
    jvmToolchain(11)

    jvm()
    macosArm64()
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.serialization.json)
                api(libs.kaml)
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
