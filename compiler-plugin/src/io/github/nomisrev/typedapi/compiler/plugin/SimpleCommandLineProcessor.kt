package io.github.nomisrev.typedapi.compiler.plugin

import io.github.nomisrev.BuildConfig
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

@Suppress("unused") // Used via reflection.
class SimpleCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = BuildConfig.KOTLIN_PLUGIN_ID

    override val pluginOptions: Collection<CliOption> = emptyList()

    private fun <T : Any> CompilerConfiguration.put(
        option: Option<T>,
        value: String,
    ): Unit = put(option.key, option.convertValue(value))

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) {
        when (val found = Options.entries.firstOrNull { it.name == option.optionName }) {
            null -> throw CliOptionProcessingException("Unknown plugin option: ${option.optionName}")
            else -> configuration.put(found, value)
        }
    }
}
