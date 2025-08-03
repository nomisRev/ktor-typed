pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        maybeCreate("libs").apply { from(files("../gradle/libs.versions.toml")) }
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.2.3")
        }
    }
    repositories {
        mavenCentral()
    }
}

rootProject.name = "integration-test"

include("ktor")
include("spring-webflux")
includeBuild("..")
