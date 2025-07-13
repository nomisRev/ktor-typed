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
    input(Path(name, validation, info))

inline fun <reified A> EndpointAPI.Path(
    name: String? = null,
    validation: Validation<A>? = null,
    info: Info<A>? = null
): Input.Path<A> = Input.Path(name, validation, A::class, typeOf<A>(), info)

inline fun <reified A> EndpointAPI.query(
    name: String? = null,
    validation: Validation<A>? = null,
    info: Info<A>? = null
): DelegateProvider<A> =
    input(Query(name, validation, info))

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
): DelegateProvider<A> = input(Header(name, toHeaderCase, validation, info))

inline fun <reified A> EndpointAPI.Header(
    name: String? = null,
    noinline toHeaderCase: (String) -> String =
        { it.split(Regex("(?=[A-Z])")).joinToString("-") { it.replaceFirstChar { c -> c.uppercase() } } },
    validation: Validation<A>? = null,
    info: Info<A>? = null
): Input.Header<A> = Input.Header(name, toHeaderCase, validation, A::class, typeOf<A>(), info)

inline fun <reified A> EndpointAPI.body(
    info: Info<A>? = null
): DelegateProvider<A> = input(Body(info))

inline fun <reified A> EndpointAPI.Body(
    info: Info<A>? = null
): Input.Body<A> = Input.Body(A::class, typeOf<A>(), info)

fun interface DelegateProvider<A> {
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, A>
}
