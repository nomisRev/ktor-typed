package io.github.nomisrev.typedapi.ktor

import io.ktor.http.HttpMethod
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import io.ktor.util.reflect.TypeInfo
import io.github.nomisrev.typedapi.DelegateProvider
import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
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
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.typeOf

public fun <A : Any> Route.route(
    typeInfo: TypeInfo,
    method: HttpMethod,
    endpoint: (EndpointAPI) -> A,
    block: suspend RoutingContext.(A) -> Unit,
) {
    val path = (typeInfo.type.annotations.filterIsInstance<Endpoint>().singleOrNull()
        ?: typeInfo.type.findAnnotation<Endpoint>())?.path
        ?: throw IllegalArgumentException("Missing @Endpoint annotation")

    route(path, method) {
        handle {
            val api = ServerEndpointAPI(this)
            val value = endpoint(api)
            runCatching {
                api.bodyInput?.let { input ->
                    api.body.completeWith(runCatching { call.receiveNullable(TypeInfo(input.kClass, input.kType)) })
                }
                block(value)
            }.onFailure { it.printStackTrace() }.getOrThrow()
        }
    }
}

public inline fun <reified A : Any> Route.get(
    noinline endpoint: (EndpointAPI) -> A,
    noinline block: suspend RoutingContext.(A) -> Unit,
) = route(typeInfo<A>(), HttpMethod.Get, endpoint, block)

public inline fun <reified A : Any> Route.post(
    noinline endpoint: (EndpointAPI) -> A,
    noinline block: suspend RoutingContext.(A) -> Unit,
) = route(typeInfo<A>(), HttpMethod.Post, endpoint, block)

public inline fun <reified A : Any> Route.route(
    method: HttpMethod,
    noinline endpoint: (EndpointAPI) -> A,
    noinline block: suspend RoutingContext.(A) -> Unit,
) = route(typeInfo<A>(), method, endpoint, block)

public inline fun <reified A : Any> Route.route(
    noinline endpoint: (EndpointAPI) -> A,
    method: HttpMethod,
    noinline block: suspend RoutingContext.(A) -> Unit,
) = route(typeInfo<A>(), method, endpoint, block)

private class ServerEndpointAPI(private val context: RoutingContext) : EndpointAPI {
    val body: CompletableDeferred<Any?> = CompletableDeferred()
    var bodyInput: Input.Body<*>? = null

    override fun <A> input(input: Input<A>): DelegateProvider<A> =
        DelegateProvider { _, _ ->
            when (input) {
                is Input.Body -> ReadOnlyProperty<Any?, A> { _, _ ->
                    @Suppress("OPT_IN_USAGE", "UNCHECKED_CAST")
                    body.getCompleted() as A
                }.also { bodyInput = input }

                is Input.Header<*> -> ReadOnlyProperty<Any?, A> { _, property ->
                    val name = input.name ?: input.casing(property.name)
                    val values = context.call.request.headers.getAll(name)
                    getParameter<A>(
                        values,
                        name,
                        input.kClass,
                        input.kType,
                        input.validation as? Validation<A>,
                        mutableListOf()
                    )
                }

                is Input.Path<*> -> ReadOnlyProperty<Any?, A> { _, property ->
                    val name = input.name ?: property.name
                    val values = context.call.pathParameters.getAll(name)
                    getParameter<A>(
                        values,
                        name,
                        input.kClass,
                        input.kType,
                        input.validation as? Validation<A>,
                        mutableListOf()
                    )
                }

                is Input.Query<*> -> ReadOnlyProperty<Any?, A> { _, property ->
                    val name = input.name ?: property.name
                    val values = context.call.queryParameters.getAll(name)
                    getParameter<A>(
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
