package io.github.nomisrev.typedapi.compiler.plugin.services

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import io.github.nomisrev.typedapi.compiler.plugin.ClassIds
import io.github.nomisrev.typedapi.compiler.plugin.Logger
import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import io.github.nomisrev.typedapi.compiler.plugin.Options
import io.github.nomisrev.typedapi.compiler.plugin.SimplePluginRegistrar
import io.github.nomisrev.typedapi.compiler.plugin.ir.MyCodeIrGenerationExtension
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.TestServices

class ExtensionRegistrarConfigurator(
    testServices: TestServices,
) : EnvironmentConfigurator(testServices) {
    override fun CompilerPluginRegistrar.ExtensionStorage.registerCompilerExtensions(
        module: TestModule,
        configuration: CompilerConfiguration,
    ) {
        val module =
            PluginContext(
                classIds = ClassIds(),
                options = Options(configuration),
                logger = Logger(true),
            )
        FirExtensionRegistrarAdapter.registerExtension(SimplePluginRegistrar(module))
        IrGenerationExtension.registerExtension(MyCodeIrGenerationExtension(module))
    }
}
