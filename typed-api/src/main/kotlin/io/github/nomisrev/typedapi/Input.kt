package io.github.nomisrev.typedapi

import kotlin.reflect.KClass
import kotlin.reflect.KType

sealed interface Input<A> {
    val kClass: KClass<*>
    val kType: KType
    val info: Info<A>?

    class Path<A>(
        val name: String?,
        val validation: Validation<A>?,
        override val kClass: KClass<*>,
        override val kType: KType,
        override val info: Info<A>?,
    ) : Input<A>

    class Query<A>(
        val name: String?,
        val validation: Validation<A>?,
        override val kClass: KClass<*>,
        override val kType: KType,
        override val info: Info<A>?,
    ) : Input<A>

    class Header<A>(
        val name: String?,
        val casing: (String) -> String,
        val validation: Validation<A>?,
        override val kClass: KClass<*>,
        override val kType: KType,
        override val info: Info<A>?,
    ) : Input<A>

    class Body<A>(
        override val kClass: KClass<*>,
        override val kType: KType,
        override val info: Info<A>?,
    ) : Input<A>

    fun name(): String? =
        when (this) {
            is Path<*> -> name
            is Query<*> -> name
            is Body<*> -> null
            is Header<*> -> name
        }
}