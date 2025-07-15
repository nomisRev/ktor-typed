plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.power.assert) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.kover)
}

group = "io.github.nomisrev.typedapi"
version = "0.0.1"

koverReport {
    defaults {
        html { onCheck = true }
    }
    verify {
        rule {
            minBound(80)
        }
    }
}

dependencies {
    kover(project(":typed-api"))
    kover(project(":ktor-typed-api"))
    kover(project(":spring-webflux-typed-api"))
}