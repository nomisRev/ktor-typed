plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.power.assert)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.testBalloon)
//    id("org.jetbrains.kotlinx.kover") version "0.7.5"
}

group = "com.example"
version = "0.0.1"

dependencies {
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.sessions)

    testImplementation(libs.testBalloon.framework.core)
//    testImplementation("io.ktor:ktor-server-test-host:3.1.3")
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
    jvmToolchain(11)
}

//koverReport {
//    defaults {
//        html { onCheck = true }
//    }
//
//    // TODO remove ApplicationKt example, and move to docs / KotlinX Knit
//    filters {
//        excludes {
//            // Exclude Application.kt from coverage as it's the entry point
//            classes("com.example.ApplicationKt")
//        }
//    }
//
////    verify {
////        rule {
////            minBound(70) // Minimum 70% line coverage
////        }
////    }
//}
