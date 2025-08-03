package io.github.nomisrev.typedapi

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

data class Route(
    val path: String,
    val inputs: List<Input<*>>
)

inline fun <reified A> collect(
    noinline block: (EndpointAPI) -> A
): Route = collect(typeOf<A>(), A::class, block)

fun <A> collect(
    kType: KType,
    kClass: KClass<*>,
    block: (EndpointAPI) -> A
): Route {
    val path = kType.annotations.filterIsInstance<Endpoint>().firstOrNull()?.path
        ?: kClass.annotations.filterIsInstance<Endpoint>().firstOrNull()?.path ?: error("Endpoint API missing.")

    val inputs = mutableListOf<Input<*>>()
    val api = object : EndpointAPI {
        override fun <A> input(input: Input<A>): ReadOnlyProperty<Any?, A> {
            val name = input.name()
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

            return ReadOnlyProperty { _, _ -> error("Not implemented") }
        }
    }
    block(api)

    return Route(path, inputs)
}