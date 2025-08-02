plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.buildconfig)
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
    test {
        java.setSrcDirs(listOf("test"))
        resources.setSrcDirs(listOf("testResources"))
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(libs.kotlin.gradle.plugin.api)
    testImplementation(libs.kotlin.test.junit5)
}

buildConfig {
    packageName(rootProject.group.toString())

    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")

    val pluginProject = project(":compiler-plugin")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${pluginProject.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${pluginProject.name}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${pluginProject.version}\"")

    val coreProject = project(":typed-api")
    buildConfigField(
        type = "String",
        name = "RUNTIME_LIBRARY_COORDINATES",
        expression = "\"${coreProject.group}:${coreProject.name}:${coreProject.version}\"",
    )
}

gradlePlugin {
    plugins {
        create("typedApi") {
            id = rootProject.group.toString()
            displayName = "TypedApiPlugin"
            description = "TypedApiPlugin"
            implementationClass = "io.github.nomisrev.typedapi.compiler.plugin.TypedApiGradlePlugin"
        }
    }
}
