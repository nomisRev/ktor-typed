plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.buildconfig)
    `java-test-fixtures`
    idea
}

sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("resources"))
    }
    testFixtures {
        java.setSrcDirs(listOf("test-fixtures"))
    }
    test {
        java.setSrcDirs(listOf("test", "test-gen"))
        resources.setSrcDirs(listOf("testData"))
    }
}

idea {
    module.generatedSourceDirs.add(projectDir.resolve("test-gen"))
}

val annotationsRuntimeClasspath: Configuration by configurations.creating { isTransitive = false }
val testDependenciesRuntimeClasspath: Configuration by configurations.creating { }

dependencies {
    compileOnly(libs.kotlin.compiler)
    compileOnly(project(":typed-api"))

    testFixturesApi(libs.kotlin.test.junit5)
    testFixturesApi(libs.kotlin.compiler.internal.test.framework)
    testFixturesApi(libs.kotlin.compiler)

    annotationsRuntimeClasspath(project(":typed-api"))
    testDependenciesRuntimeClasspath(ktorLibs.server.core)

    // Dependencies required to run the internal test framework.
    testRuntimeOnly(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.kotlin.reflect)
    testRuntimeOnly(libs.kotlin.test)
    testRuntimeOnly(libs.kotlin.script.runtime)
    testRuntimeOnly(libs.kotlin.annotations.jvm)
}

buildConfig {
    useKotlinOutput {
        internalVisibility = true
    }

    packageName(rootProject.group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.group}\"")
}

tasks.test {
    dependsOn(annotationsRuntimeClasspath)
    dependsOn(testDependenciesRuntimeClasspath)

    useJUnitPlatform()
    workingDir = rootDir

    systemProperty("annotationsRuntime.classpath", annotationsRuntimeClasspath.asPath)
    systemProperty("testDependenciesRuntime.classpath", testDependenciesRuntimeClasspath.asPath)

    // Properties required to run the internal test framework.
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")

    systemProperty("idea.ignore.disabled.plugins", "true")
    systemProperty("idea.home.path", rootDir)
}

kotlin {
    jvmToolchain(11)
    compilerOptions {
        optIn.add("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
        optIn.add("org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI")
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

val generateTests by tasks.registering(JavaExec::class) {
    inputs
        .dir(layout.projectDirectory.dir("testData"))
        .withPropertyName("testData")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    outputs
        .dir(layout.projectDirectory.dir("test-gen"))
        .withPropertyName("generatedTests")

    classpath = sourceSets.testFixtures.get().runtimeClasspath
    mainClass.set("io.github.nomisrev.typedapi.compiler.plugin.GenerateTestsKt")
    workingDir = rootDir
}

tasks.compileTestKotlin {
    dependsOn(generateTests)
}

fun Test.setLibraryProperty(
    propName: String,
    jarName: String,
) {
    val path =
        project.configurations
            .testRuntimeClasspath
            .get()
            .files
            .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
            ?.absolutePath
            ?: return
    systemProperty(propName, path)
}
