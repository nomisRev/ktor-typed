package io.ktor.route.fastapi

import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.routing.RoutingContext
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
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

interface InputDelegate<A> : ReadOnlyProperty<Any?, A> {
    val kClass: KClass<*>
    val kType: KType
    val info: Info<A>?

    class Path<A>(
        val name: String?,
        val validation: Validation<A>?,
        override val kClass: KClass<*>,
        override val kType: KType,
        override val info: Info<A>?,
        val context: RoutingContext,
    ) : InputDelegate<A> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): A {
            val name = name ?: property.name
            val values = context.call.pathParameters.getAll(name)
            return getParameter(values, name, kClass, kType, validation, mutableListOf())
        }
    }

    class Query<A>(
        val name: String?,
        val validation: Validation<A>?,
        override val kClass: KClass<*>,
        override val kType: KType,
        override val info: Info<A>?,
        val context: RoutingContext,
    ) : InputDelegate<A> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): A {
            val name = name ?: property.name
            val values = context.call.queryParameters.getAll(name)
            return getParameter(values, name, kClass, kType, validation, mutableListOf())
        }
    }

    class Header<A>(
        val name: String?,
        val casing: (String) -> String,
        val validation: Validation<A>?,
        override val kClass: KClass<*>,
        override val kType: KType,
        override val info: Info<A>?,
        val context: RoutingContext,
    ) : InputDelegate<A> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): A {
            val name = name ?: casing(property.name)
            val values = context.call.request.headers.getAll(name)
            return getParameter(values, name, kClass, kType, validation, mutableListOf())
        }
    }

    class Body<A>(
        override val kClass: KClass<*>,
        override val kType: KType,
        val deferred: CompletableDeferred<A>,
        override val info: Info<A>?,
    ) : InputDelegate<A> {
        @OptIn(ExperimentalCoroutinesApi::class)
        override fun getValue(thisRef: Any?, property: KProperty<*>): A = deferred.getCompleted()
    }
}

private fun <A> getParameter(
    values: List<String>?,
    name: String,
    kClass: KClass<*>,
    kType: KType,
    validation: Validation<A>?,
    builder: ValidationBuilder
): A = when {
    values == null && kType.isMarkedNullable -> null as A
    values == null -> throw MissingRequestParameterException(name)
    else -> {
        val value: Any? = try {
            @Suppress("UNCHECKED_CAST")
            DefaultConversionService.fromValues(values, TypeInfo(kClass, kType))
        } catch (cause: Exception) {
            throw ParameterConversionException(name, kClass.simpleName ?: kType.toString(), cause)
        }
        if (validation != null) builder.validateParameter(value, validation as Validation<Any?>, name)
        value as A
    }
}

private fun <A> ValidationBuilder.validateParameter(value: A, validation: Validation<A>, parameterName: String): A {
    val validatedValue = with(validation) { validate(value) }
    return if (isNotEmpty()) throw BadRequestException("Parameter '$parameterName' validation failed: ${joinToString()}")
    else validatedValue
}
