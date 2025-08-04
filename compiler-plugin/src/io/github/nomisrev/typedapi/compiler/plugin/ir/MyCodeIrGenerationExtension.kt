package io.github.nomisrev.typedapi.compiler.plugin.ir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class MyCodeIrGenerationExtension(private val module: PluginContext) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        val symbols = IrSymbols(pluginContext, module)
        moduleFragment.accept(ValueConstructorIrExtension(pluginContext, module, symbols), null)
        moduleFragment.accept(HttpRequestValueExtension(pluginContext, module, symbols), null)
        moduleFragment.accept(EndpointFactoryIrExtension(pluginContext, module, symbols), null)
    }
}