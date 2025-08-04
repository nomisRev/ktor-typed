package io.github.nomisrev.typedapi.compiler.plugin

import io.github.nomisrev.typedapi.compiler.plugin.fir.AddSupertypeExtension
import io.github.nomisrev.typedapi.compiler.plugin.fir.EndpointFactoryExtension
import io.github.nomisrev.typedapi.compiler.plugin.fir.FirCheckers
import io.github.nomisrev.typedapi.compiler.plugin.fir.HttpRequestValueExtension
import io.github.nomisrev.typedapi.compiler.plugin.fir.ValueConstructorExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class SimplePluginRegistrar(
    val context: PluginContext,
) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::FirCheckers.bind(context)
        +::AddSupertypeExtension.bind(context)
        +::EndpointFactoryExtension.bind(context)
        +::HttpRequestValueExtension.bind(context)
        +::ValueConstructorExtension.bind(context)
    }
}
