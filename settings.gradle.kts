pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.2.1")
        }
    }
}

rootProject.name = "typed-api-root"
include("typed-api")
include("typed-api-docs")
include("parser")
include("ktor-typed-api")
include("spring-webflux-typed-api")
include("compiler-plugin")
include("gradle-plugin")
