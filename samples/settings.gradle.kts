pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        maybeCreate("libs").apply { from(files("../gradle/libs.versions.toml")) }
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.2.1")
        }
    }
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "samples"

include("app")
includeBuild("..")
