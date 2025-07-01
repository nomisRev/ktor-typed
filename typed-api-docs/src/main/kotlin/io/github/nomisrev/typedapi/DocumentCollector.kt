package io.github.nomisrev.typedapi

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

data class Route(val path: String, val inputs: List<Input<*>>)

inline fun <reified A> collect(
    kType: KType = typeOf<A>(),
    kClass: KClass<*> = A::class,
    block: (EndpointAPI) -> A
): Route {
    val path = kType.annotations.filterIsInstance<Endpoint>().firstOrNull()?.path
        ?: kClass.annotations.filterIsInstance<Endpoint>().firstOrNull()?.path ?: error("Endpoint API missing.")

    val inputs = mutableListOf<Input<*>>()
    val api = object : EndpointAPI {
        override fun <A> input(input: Input<A>): DelegateProvider<A> = DelegateProvider { _, prop ->
            val name = input.name() ?: prop.name
            inputs.add(
                when (input) {
                    is Input.Body<*> -> input
                    is Input.Header<*> -> Input.Header(
                        name,
                        input.casing,
                        input.validation as? Validation<A>,
                        input.kClass,
                        input.kType,
                        input.info
                    )

                    is Input.Path<*> -> Input.Path(
                        name,
                        input.validation as? Validation<A>,
                        input.kClass,
                        input.kType,
                        input.info
                    )

                    is Input.Query<*> -> Input.Query(
                        name,
                        input.validation as? Validation<A>,
                        input.kClass,
                        input.kType,
                        input.info
                    )
                }
            )

            ReadOnlyProperty { _, _ -> error("Not implemented") }
        }
    }
    block(api)

    return Route(path, inputs)
}