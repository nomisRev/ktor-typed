package io.github.nomisrev.typedapi

import kotlin.properties.ReadOnlyProperty

// TODO better name?
class MapEndpointAPI(private val values: Map<String, Any?>) : EndpointAPI {
    constructor(vararg values: Pair<String, Any?>) : this(values.toMap())

    override fun <A> input(input: Input<A>): ReadOnlyProperty<Any?, A> =
        ReadOnlyProperty { _, prop ->
            @Suppress("UNCHECKED_CAST")
            values[prop.name] as A
        }
}
