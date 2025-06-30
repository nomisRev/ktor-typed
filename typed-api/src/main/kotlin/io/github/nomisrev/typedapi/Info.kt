package io.github.nomisrev.typedapi

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

data class Example<T>(val value: T, val serializer: KSerializer<T>)

data class Info<T>(
    val title: String? = null,
    val description: String? = null,
    val example: Example<T>? = null,
    val regex: String? = null,
    val deprecated: Boolean = false,
)

inline fun <reified T> Info<T>.example(value: T) =
    example(value, serializer<T>())

fun <T> Info<T>.example(value: T, serializer: KSerializer<T>) =
    copy(example = Example(value, serializer))