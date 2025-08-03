package io.github.nomisrev.typedapi.compiler.plugin

class PluginContext(
    val classIds: ClassIds,
    val callableIds: CallableIds,
    val options: Options,
    val logger: Logger,
)
