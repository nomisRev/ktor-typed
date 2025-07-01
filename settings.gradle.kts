enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
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

rootProject.name = "typed-api-root"
include("typed-api")
include("typed-api-docs")
include("parser")
include("ktor-typed-api")
include("spring-webflux-typed-api")
include("typed-api-ksp")
