package io.github.nomisrev.typedapi

import io.ktor.http.ContentType
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.typeOf

sealed interface InputDelegate<A> : ReadOnlyProperty<Any?, A> {
    fun interface Query<A> : InputDelegate<A>
    fun interface Path<A> : InputDelegate<A>
    fun interface Header<A> : InputDelegate<A>
    fun interface Body<A> : InputDelegate<A>
}

interface EndpointAPI {
    fun <A> input(input: Input<A>): ReadOnlyProperty<Any?, A>
}

inline fun <reified A> EndpointAPI.path(
    name: String? = null,
    validation: Validation<A>? = null,
    info: Info<A>? = null
): ReadOnlyProperty<Any?, A> =
    input(Path(name, validation, info))

inline fun <reified A> EndpointAPI.query(
    name: String? = null,
    validation: Validation<A>? = null,
    info: Info<A>? = null
): ReadOnlyProperty<Any?, A> =
    input(Query(name, validation, info))

inline fun <reified A> EndpointAPI.header(
    name: String? = null,
    noinline toHeaderCase: (String) -> String =
        { it.split(Regex("(?=[A-Z])")).joinToString("-") { it.replaceFirstChar { c -> c.uppercase() } } },
    validation: Validation<A>? = null,
    info: Info<A>? = null
): ReadOnlyProperty<Any?, A> = input(Header(name, toHeaderCase, validation, info))

inline fun <reified A> EndpointAPI.body(
    contentType: ContentType,
    info: Info<A>? = null
): ReadOnlyProperty<Any?, A> = input(Body(contentType, info))

inline fun <reified A> Path(
    name: String? = null,
    validation: Validation<A>? = null,
    info: Info<A>? = null
): Input.Path<A> = Input.Path(name, validation, A::class, typeOf<A>(), info)

inline fun <reified A> Header(
    name: String? = null,
    noinline toHeaderCase: (String) -> String =
        { it.split(Regex("(?=[A-Z])")).joinToString("-") { it.replaceFirstChar { c -> c.uppercase() } } },
    validation: Validation<A>? = null,
    info: Info<A>? = null
): Input.Header<A> = Input.Header(name, toHeaderCase, validation, A::class, typeOf<A>(), info)

inline fun <reified A> Query(
    name: String? = null,
    validation: Validation<A>? = null,
    info: Info<A>? = null
): Input.Query<A> =
    Input.Query(name, validation, A::class, typeOf<A>(), info)


inline fun <reified A> Body(
    contentType: ContentType,
    info: Info<A>? = null
): Input.Body<A> = Input.Body(contentType, A::class, typeOf<A>(), info)
