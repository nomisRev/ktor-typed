package io.github.nomisrev.typedapi

import kotlin.reflect.KProperty1

/**
 * Creates a map of property names to KProperty1 objects.
 * This function is used to create a map that can be used in the Request constructor.
 */
fun <T> properties(vararg properties: KProperty1<T, *>): Map<String, KProperty1<T, *>> =
    properties.associateBy { it.name }