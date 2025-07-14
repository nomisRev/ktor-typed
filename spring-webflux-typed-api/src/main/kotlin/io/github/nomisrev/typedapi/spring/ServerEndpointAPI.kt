package io.github.nomisrev.typedapi.spring

import io.github.nomisrev.typedapi.DelegateProvider
import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.Validation
import io.github.nomisrev.typedapi.ValidationBuilder
import kotlinx.coroutines.reactor.mono
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

public fun <A : Any> route(
    typeInfo: KClass<A>,
    method: HttpMethod,
    endpoint: (EndpointAPI) -> A,
    block: suspend ServerRequest.(A) -> Mono<ServerResponse>,
): RouterFunction<ServerResponse> {
    val endpointAnnotation =
        typeInfo.findAnnotation<Endpoint>() ?: throw IllegalArgumentException("Missing @Endpoint annotation")

    val fullPath = endpointAnnotation.path

    val predicate: RequestPredicate = when (method) {
        HttpMethod.GET -> RequestPredicates.GET(fullPath)
        HttpMethod.POST -> RequestPredicates.POST(fullPath)
        HttpMethod.PUT -> RequestPredicates.PUT(fullPath)
        HttpMethod.DELETE -> RequestPredicates.DELETE(fullPath)
        HttpMethod.PATCH -> RequestPredicates.PATCH(fullPath)
        HttpMethod.HEAD -> RequestPredicates.HEAD(fullPath)
        HttpMethod.OPTIONS -> RequestPredicates.OPTIONS(fullPath)
        else -> throw IllegalStateException("")
    }

    return RouterFunctions.route(predicate) { request ->
        val api = ServerEndpointAPI(fullPath, request)
        val value = endpoint(api)

        val bodyMono = api.bodyInput?.let { input ->
            val bodyClass = input.kClass.java
            request.bodyToMono(bodyClass)
        } ?: Mono.empty()

        if (api.bodyInput == null) {
            // No body expected, just execute the block
            mono {
                request.block(value)
            }.flatMap { it }
        } else {
            // Body expected, wait for it
            bodyMono.flatMap { body ->
                mono {
                    api.body = body
                    request.block(value)
                }.flatMap { it }
            }
        }
    }
}

public inline fun <reified A : Any> RouterFunctionDsl.GET(
    noinline endpoint: (EndpointAPI) -> A,
    noinline block: suspend ServerRequest.(A) -> Mono<ServerResponse>,
) = add(route(A::class, HttpMethod.GET, endpoint, block))

public inline fun <reified A : Any> RouterFunctionDsl.POST(
    noinline endpoint: (EndpointAPI) -> A,
    noinline block: suspend ServerRequest.(A) -> Mono<ServerResponse>,
) = add(route(A::class, HttpMethod.POST, endpoint, block))

public inline fun <reified A : Any> route(
    method: HttpMethod,
    noinline endpoint: (EndpointAPI) -> A,
    noinline block: suspend ServerRequest.(A) -> Mono<ServerResponse>,
): RouterFunction<ServerResponse> = route(A::class, method, endpoint, block)

public inline fun <reified A : Any> route(
    noinline endpoint: (EndpointAPI) -> A,
    method: HttpMethod,
    noinline block: suspend ServerRequest.(A) -> Mono<ServerResponse>,
): RouterFunction<ServerResponse> = route(A::class, method, endpoint, block)

private class ServerEndpointAPI(var path: String, private val request: ServerRequest) : EndpointAPI {
    var body: Any? = null
    var bodyInput: Input.Body<*>? = null

    override fun <A> input(input: Input<A>): DelegateProvider<A> =
        DelegateProvider { _, _ ->
            when (input) {
                is Input.Body -> ReadOnlyProperty<Any?, A> { _, _ ->
                    @Suppress("UNCHECKED_CAST")
                    body as A
                }.also { bodyInput = input }

                is Input.Header<*> -> ReadOnlyProperty<Any?, A> { _, property ->
                    val name = input.name ?: input.casing(property.name)
                    val values = request.headers().header(name)
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
                    val value = request.pathVariable(name)
                    getParameter<A>(
                        listOf(value),
                        name,
                        input.kClass,
                        input.kType,
                        input.validation as? Validation<A>,
                        mutableListOf()
                    )
                }

                is Input.Query<*> -> ReadOnlyProperty<Any?, A> { _, property ->
                    val name = input.name ?: property.name
                    val values = request.queryParams()[name]
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

@Suppress("UNCHECKED_CAST")
private fun <A> getParameter(
    values: List<String>?,
    name: String,
    kClass: KClass<*>,
    kType: KType,
    validation: Validation<A>?,
    builder: ValidationBuilder
): A = when {
    values == null && kType.isMarkedNullable -> null as A
    values == null -> throw IllegalArgumentException("Missing parameter: $name")
    else -> {
        val value: Any? = try {
            convertValue(values, kClass, kType)
        } catch (cause: Exception) {
            throw IllegalArgumentException("Cannot convert parameter '$name' to ${kClass.simpleName}", cause)
        }
        if (validation != null) builder.validateParameter(value, validation as Validation<Any?>, name)
        value as A
    }
}

private fun convertValue(values: List<String>, kClass: KClass<*>, kType: KType): Any? {
    val firstValue = values.firstOrNull() ?: return null

    return when (kClass) {
        String::class -> firstValue
        Int::class -> firstValue.toInt()
        Long::class -> firstValue.toLong()
        Double::class -> firstValue.toDouble()
        Float::class -> firstValue.toFloat()
        Boolean::class -> firstValue.toBoolean()
        Short::class -> firstValue.toShort()
        Byte::class -> firstValue.toByte()
        else -> if (kType.isMarkedNullable && firstValue.isEmpty()) null
        else TODO()
    }
}

private fun <A> ValidationBuilder.validateParameter(value: A, validation: Validation<A>, parameterName: String): A {
    val validatedValue = with(validation) { validate(value) }
    return if (isNotEmpty()) throw IllegalArgumentException("Parameter '$parameterName' validation failed: ${joinToString()}")
    else validatedValue
}