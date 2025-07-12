package io.github.nomisrev.typedapi.compiler.plugin

import io.github.nomisrev.typedapi.compiler.plugin.fir.FirCheckers
import io.github.nomisrev.typedapi.compiler.plugin.fir.MyCodeGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class SimplePluginRegistrar(
    val logger: PluginContext,
) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::FirCheckers.bind(logger)
        +::MyCodeGenerationExtension.bind(logger)
    }
}
