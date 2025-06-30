plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.power-assert") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"
    id("org.jetbrains.kotlinx.kover") version "0.7.5"
//    id("de.infix.testBalloon") version "0.3.0-K2.1.21"
}

group = "com.example"
version = "0.0.1"


kotlin {
    compilerOptions.freeCompilerArgs.add("-Xcontext-parameters")
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

koverReport {
    defaults {
        html { onCheck = true }
    }
    verify {
        rule {
            minBound(90)
        }
    }
}

dependencies {
    kover(project(":ktor-typed-api"))
}