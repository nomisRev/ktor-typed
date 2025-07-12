plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.buildconfig)
    id("java-gradle-plugin")
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

    val annotationsProject = project(":typed-api")
    buildConfigField(
        type = "String",
        name = "ANNOTATIONS_LIBRARY_COORDINATES",
        expression = "\"${annotationsProject.group}:${annotationsProject.name}:${annotationsProject.version}\"",
    )
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = rootProject.group.toString()
            displayName = "SimplePlugin"
            description = "SimplePlugin"
            implementationClass = "io.github.nomisrev.typedapi.compiler.plugin.SimpleGradlePlugin"
        }
    }
}
