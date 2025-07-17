pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.2.1")
        }
    }
    repositories {
        mavenCentral()
        google()
    }
}

apply(from = "../version-catalog-settings.gradle.kts")

rootProject.name = "samples"

include("app")
includeBuild("..")
