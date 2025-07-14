package io.github.nomisrev.typedapi

import kotlin.properties.ReadOnlyProperty

class MapEndpointAPI(private val values: Map<String, Any?>) : EndpointAPI {
    override fun <A> input(input: Input<A>): DelegateProvider<A> =
        DelegateProvider { _, prop ->
            ReadOnlyProperty { _, _ ->
                values[prop.name] as A
            }
        }
}
