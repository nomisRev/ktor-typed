package io.github.nomisrev.typedapi.compiler.plugin

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@Suppress("LongParameterList")
class Option<A : Any>(
    val name: String,
    override val valueDescription: String,
    override val description: String,
    override val required: Boolean,
    override val allowMultipleOccurrences: Boolean,
    val defaultValue: A,
    val convertValue: (String) -> A,
) : AbstractCliOption {
    val key = CompilerConfigurationKey<A>(name)
    override val optionName: String
        get() = name

    companion object {
        val Debug =
            Option(
                name = "debug",
                valueDescription = "<true|false>",
                description = "Debug mode",
                required = false,
                allowMultipleOccurrences = false,
                defaultValue = true,
                convertValue = { it.toBoolean() },
            )
    }
}

private operator fun <T : Any> CompilerConfiguration.get(option: Option<T>): T = get(option.key, option.defaultValue)

data class Options(
    val debug: Boolean,
) {
    constructor(configuration: CompilerConfiguration) : this(
        configuration[Option.Debug],
    )

    companion object {
        val entries = listOf(Option.Debug)
    }
}
