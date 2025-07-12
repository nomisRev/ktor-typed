package io.github.nomisrev.typedapi.compiler.plugin.services

import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.RuntimeClasspathProvider
import org.jetbrains.kotlin.test.services.TestServices
import java.io.File

private val annotationsRuntimeClasspath: List<File> =
    System.getProperty("annotationsRuntime.classpath")?.split(File.pathSeparator)?.map(::File)
        ?: error("Unable to get a valid classpath from 'annotationsRuntime.classpath' property")

private val testDependenciesRuntimeClasspath: List<File> =
    System.getProperty("testDependenciesRuntime.classpath")?.split(File.pathSeparator)?.map(::File)
        ?: error("Unable to get a valid classpath from 'testDependenciesRuntime.classpath' property")

class PluginAnnotationsProvider(
    testServices: TestServices,
) : EnvironmentConfigurator(testServices) {
    override fun configureCompilerConfiguration(
        configuration: CompilerConfiguration,
        module: TestModule,
    ) {
        val all = annotationsRuntimeClasspath + testDependenciesRuntimeClasspath
        configuration.addJvmClasspathRoots(all)
    }
}

class RuntimeClassPathProvider(
    testServices: TestServices,
) : RuntimeClasspathProvider(testServices) {
    override fun runtimeClassPaths(module: TestModule): List<File> = annotationsRuntimeClasspath + testDependenciesRuntimeClasspath
}
