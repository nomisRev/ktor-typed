package io.github.nomisrev.typedapi.spring

import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.EndpointFactory
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.Validation
import io.github.nomisrev.typedapi.ValidationBuilder
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KType

public fun <A : Any> route(
    method: HttpMethod,
    factory: EndpointFactory<A>,
    block: suspend ServerRequest.(A) -> Mono<ServerResponse>,
) = factory.create { path, endpoint ->
    val predicate: RequestPredicate = when (method) {
        HttpMethod.GET -> RequestPredicates.GET(path)
        HttpMethod.POST -> RequestPredicates.POST(path)
        HttpMethod.PUT -> RequestPredicates.PUT(path)
        HttpMethod.DELETE -> RequestPredicates.DELETE(path)
        HttpMethod.PATCH -> RequestPredicates.PATCH(path)
        HttpMethod.HEAD -> RequestPredicates.HEAD(path)
        HttpMethod.OPTIONS -> RequestPredicates.OPTIONS(path)
        else -> throw IllegalStateException("")
    }

    RouterFunctions.route(predicate) { request ->
        val api = ServerEndpointAPI(path, request)
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

public fun <A : Any> RouterFunctionDsl.GET(
    factory: EndpointFactory<A>,
    block: suspend ServerRequest.(A) -> Mono<ServerResponse>,
) = add(route(HttpMethod.GET, factory, block))

public fun <A : Any> RouterFunctionDsl.POST(
    factory: EndpointFactory<A>,
    block: suspend ServerRequest.(A) -> Mono<ServerResponse>,
) = add(route(HttpMethod.POST, factory, block))

private class ServerEndpointAPI(var path: String, private val request: ServerRequest) : EndpointAPI {
    var body: Any? = null
    var bodyInput: Input.Body<*>? = null

    override fun <A> input(input: Input<A>): ReadOnlyProperty<Any?, A> =
        when (input) {
            is Input.Body -> ReadOnlyProperty<Any?, A> { _, _ ->
                @Suppress("UNCHECKED_CAST")
                body as A
            }.also { bodyInput = input }

            is Input.Header<*> -> ReadOnlyProperty<Any?, A> { _, property ->
                val name = input.casing(input.name!!)
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