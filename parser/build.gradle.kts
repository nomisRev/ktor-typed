plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.serialization)
    id("org.jetbrains.kotlinx.kover")
}

kotlin {
    jvm()
    macosArm64()
    linuxX64()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.serialization)
                api(libs.kotlinx.serialization.json)
                api(libs.kaml)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
