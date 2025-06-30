package io.github.nomisrev.typedapi.ktor

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.ktor.server.plugins.MissingRequestParameterException
import io.github.nomisrev.typedapi.DelegateProvider
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.Input
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class ClientAPI<A : Any, B : Any>(
    val request: Request<A, B>,
    path: String,
    val builder: HttpRequestBuilder
) : EndpointAPI {
    private var currentUrl = path
    val properties = request.properties

    fun build(): Unit {
        request.create(this)
        builder.url.takeFrom(currentUrl)
    }

    override fun <A> input(input: Input<A>): DelegateProvider<A> = DelegateProvider { _, prop ->
        val name = input.name() ?: prop.name
        val property = properties[prop.name] ?: throw IllegalArgumentException("Property ${prop.name} not found")
        val value = property.get(request.input) as? A
        when (input) {
            // TODO: How to parameterise ContentType.
            is Input.Body<*> -> value?.let {
                builder.contentType(ContentType.Application.Json)
                builder.setBody(value)
            }

            is Input.Path<*> -> currentUrl = currentUrl.replace("{${name}}", value.toString())
            is Input.Header<*> -> builder.header(name, value)
            is Input.Query<*> -> builder.parameter(name, value)
        }

        ReadOnlyProperty { _, _ ->
            if (value == null && input.kType.isMarkedNullable) null as A
            else value ?: throw MissingRequestParameterException(name)
        }
    }
}

class Request<A, B>(val input: A, val create: (EndpointAPI) -> B, val properties: Map<String, KProperty1<A, *>>)

inline fun <reified A : Any, B> Request(input: A, noinline create: (EndpointAPI) -> B) =
    Request(input, create, A::class.memberProperties.associateBy { it.name })

suspend fun <A : Any, B : Any> HttpClient.get(path: String, request: Request<A, B>): HttpResponse {
    return request {
        method = HttpMethod.Get
        ClientAPI(request, path, this).build()
    }
}

suspend fun <A : Any, B : Any> HttpClient.post(path: String, request: Request<A, B>): HttpResponse {
    return request {
        method = HttpMethod.Post
        ClientAPI(request, path, this).build()
    }
}