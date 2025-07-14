package io.github.nomisrev.typedapi.compiler.plugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import io.github.nomisrev.typedapi.compiler.plugin.ir.MyCodeIrGenerationExtension
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

class SimplePluginComponentRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val options = Options(configuration)
        val logger = Logger(options.debug)
        val module =
            PluginContext(
                classIds = ClassIds(),
                options = options,
                logger = logger,
            )
        logger.log { "SimplePluginComponentRegistrar loaded" }
        FirExtensionRegistrarAdapter.registerExtension(SimplePluginRegistrar(module))
        IrGenerationExtension.registerExtension(MyCodeIrGenerationExtension(module))
    }
}
