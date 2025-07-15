package io.github.nomisrev.typedapi

import kotlin.properties.ReadOnlyProperty

class MapEndpointAPI(private val values: Map<String, Any?>) : EndpointAPI {
    constructor(vararg values: Pair<String, Any?>) : this(values.toMap())

    override fun <A> input(input: Input<A>): DelegateProvider<A> =
        DelegateProvider { _, prop ->
            ReadOnlyProperty { _, _ ->
                @Suppress("UNCHECKED_CAST")
                values[prop.name] as A
            }
        }
}
