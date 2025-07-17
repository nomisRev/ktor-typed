val propertiesKotlinVersion: String by settings

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))

            val kotlinVersion = gradle.startParameter.projectProperties["kotlinVersion"]
                ?: System.getenv("KOTLIN_VERSION")
                ?: propertiesKotlinVersion

            val kotlinCompilerVersion =
                gradle.startParameter.projectProperties["kotlinCompilerVersion"]
                    ?: System.getenv("KOTLIN_COMPILER_VERSION")
                    ?: kotlinVersion

            version("kotlin", kotlinVersion)
            version("kotlin-compiler", kotlinCompilerVersion)

            library("kotlin-script-runtime", "org.jetbrains.kotlin", "kotlin-script-runtime").versionRef("kotlin")
            library("kotlin-annotations-jvm", "org.jetbrains.kotlin", "kotlin-annotations-jvm").versionRef("kotlin")
            library("kotlin-gradle-plugin-api", "org.jetbrains.kotlin", "kotlin-gradle-plugin-api").versionRef("kotlin")
            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test").versionRef("kotlin")
            library("kotlin-test-junit5", "org.jetbrains.kotlin", "kotlin-test-junit5").versionRef("kotlin")
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")

            library("kotlin-compiler", "org.jetbrains.kotlin", "kotlin-compiler").versionRef("kotlin-compiler")
            library(
                "kotlin-compiler-internal-test-framework",
                "org.jetbrains.kotlin",
                "kotlin-compiler-internal-test-framework"
            ).versionRef("kotlin")

            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")
            plugin("serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
            plugin("power-assert", "org.jetbrains.kotlin.plugin.power-assert").versionRef("kotlin")
        }
    }
}