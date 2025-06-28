
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

rootProject.name = "ktor-tapir"
//include("ktor-route-simple")
//include("ktor-route-fastapi")
include("ktor-route-delegation-api")
include("ktor-route-fast-api")
include("ktor-auth")
include("ktor-yaml-converter")
