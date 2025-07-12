package io.github.nomisrev.typedapi

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

interface EndpointAPI {
    fun <A> input(input: Input<A>): DelegateProvider<A>
}

inline fun <reified A> EndpointAPI.path(
    name: String? = null,
    validation: Validation<A>? = null,
    info: Info<A>? = null
): DelegateProvider<A> =
    input(Input.Path(name, validation, A::class, typeOf<A>(), info))

inline fun <reified A> EndpointAPI.query(
    name: String? = null,
    validation: Validation<A>? = null,
    info: Info<A>? = null
): DelegateProvider<A> =
    input(Input.Query(name, validation, A::class, typeOf<A>(), info))

inline fun <reified A> EndpointAPI.Query(
    name: String? = null,
    validation: Validation<A>? = null,
    info: Info<A>? = null
): Input.Query<A> =
    Input.Query(name, validation, A::class, typeOf<A>(), info)

inline fun <reified A> EndpointAPI.header(
    name: String? = null,
    noinline toHeaderCase: (String) -> String =
        { it.split(Regex("(?=[A-Z])")).joinToString("-") { it.replaceFirstChar { c -> c.uppercase() } } },
    validation: Validation<A>? = null,
    info: Info<A>? = null
): DelegateProvider<A> = input(Input.Header(name, toHeaderCase, validation, A::class, typeOf<A>(), info))

inline fun <reified A> EndpointAPI.body(
    info: Info<A>? = null
): DelegateProvider<A> = input(Input.Body(A::class, typeOf<A>(), info))

fun interface DelegateProvider<A> {
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, A>
}
