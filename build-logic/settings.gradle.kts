dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
    }
}

apply(from = "../version-catalog-settings.gradle.kts")

rootProject.name = "build-logic"