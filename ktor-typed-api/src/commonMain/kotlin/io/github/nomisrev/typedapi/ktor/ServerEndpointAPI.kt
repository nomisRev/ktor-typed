package io.github.nomisrev.typedapi.ktor

import io.ktor.http.HttpMethod
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import io.ktor.util.reflect.TypeInfo
import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.EndpointFactory
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.Validation
import io.github.nomisrev.typedapi.ValidationBuilder
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.completeWith
import kotlin.collections.mutableListOf
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

public fun <A : Any> Route.route(
    method: HttpMethod,
    factory: EndpointFactory<A>,
    block: suspend RoutingContext.(A) -> Unit,
): Route = factory.create<Route> { path, endpoint ->
    route(path, method) {
        handle {
            val api = ServerEndpointAPI(this)
            val value = endpoint(api)
            api.bodyInput?.let { input ->
                api.body.completeWith(runCatching { call.receiveNullable(TypeInfo(input.kClass, input.kType)) })
            }
            block(value)
        }
    }
}

public fun <A : Any> Route.get(
    factory: EndpointFactory<A>,
    block: suspend RoutingContext.(A) -> Unit,
) = route(HttpMethod.Get, factory, block)

public fun <A : Any> Route.post(
    factory: EndpointFactory<A>,
    block: suspend RoutingContext.(A) -> Unit,
) = route(HttpMethod.Post, factory, block)

private class ServerEndpointAPI(private val context: RoutingContext) : EndpointAPI {
    val body: CompletableDeferred<Any?> = CompletableDeferred()
    var bodyInput: Input.Body<*>? = null

    override fun <A> input(input: Input<A>): ReadOnlyProperty<Any?, A> = when (input) {
        is Input.Body -> ReadOnlyProperty<Any?, A> { _, _ ->
            @Suppress("OPT_IN_USAGE", "UNCHECKED_CAST")
            body.getCompleted() as A
        }.also { bodyInput = input }

        is Input.Header<*> -> ReadOnlyProperty { _, _ ->
            val name = input.casing(input.name!!)
            val values = context.call.request.headers.getAll(name)
            getParameter(
                values,
                name,
                input.kClass,
                input.kType,
                input.validation as? Validation<A>,
                mutableListOf()
            )
        }

        is Input.Path<*> -> ReadOnlyProperty { _, property ->
            val name = input.name ?: property.name
            val values = context.call.pathParameters.getAll(name)
            getParameter(
                values,
                name,
                input.kClass,
                input.kType,
                input.validation as? Validation<A>,
                mutableListOf()
            )
        }

        is Input.Query<*> -> ReadOnlyProperty { _, property ->
            val name = input.name ?: property.name
            val values = context.call.queryParameters.getAll(name)
            getParameter(
                values,
                name,
                input.kClass,
                input.kType,
                input.validation as? Validation<A>,
                mutableListOf()
            )
        }
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
