package io.github.nomisrev.typedapi

import kotlin.reflect.KClass
import kotlin.reflect.KType

sealed interface Input<A> {
    val kClass: KClass<*>
    val kType: KType
    val info: Info<A>?

    fun withName(newName: String): Input<A> = when (this) {
        is Body<A> -> this
        is Header<A> -> Header(newName, casing, validation, kClass, kType, info)
        is Path<A> -> Path(newName, validation, kClass, kType, info)
        is Query<A> -> Query(newName, validation, kClass, kType, info)
    }

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

    fun name(): String? = when (this) {
        is Path<*> -> name
        is Query<*> -> name
        is Body<*> -> null
        is Header<*> -> name
    }
}