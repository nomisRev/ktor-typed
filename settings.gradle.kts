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
include("ktor-route-fastapi")
include("ktor-route-delegation-api")
project(":ktor-route-delegation-api").projectDir = file("ktor-route-delegationa-pi")
include("ktor-auth")
include("ktor-yaml-converter")
