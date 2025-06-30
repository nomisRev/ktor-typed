package io.github.nomisrev.typedapi

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty1

class Request<A, B>(val value: A, val create: (EndpointAPI) -> B, val properties: Map<String, KProperty1<A, *>>) {
    fun build(builder: (name: String, value: Any?, input: Input<*>) -> Unit) {
        create(object : EndpointAPI {
            override fun <A> input(input: Input<A>): DelegateProvider<A> = DelegateProvider { _, prop ->
                val name = input.name() ?: prop.name
                val property =
                    properties[prop.name] ?: throw IllegalArgumentException("Property ${prop.name} not found")
                val value = property.get(value) as? A
                builder(name, value, input)

                ReadOnlyProperty { _, _ ->
                    if (value == null && input.kType.isMarkedNullable) null as A
                    else value ?: throw IllegalStateException(name)
                }
            }
        })
    }
}
