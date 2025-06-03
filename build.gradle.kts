plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.power-assert") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"
    id("org.jetbrains.kotlinx.kover") version "0.7.5"
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

    // TODO remove ApplicationKt example, and move to docs / KotlinX Knit
    filters {
        excludes {
            // Exclude Application.kt from coverage as it's the entry point
            classes("com.example.ApplicationKt")
        }
    }

//    verify {
//        rule {
//            minBound(70) // Minimum 70% line coverage
//        }
//    }
}
