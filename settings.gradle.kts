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
}

rootProject.name = "typed-api"
include("typed-api")
include("ktor-typed-api")
include("typed-api-ksp")
