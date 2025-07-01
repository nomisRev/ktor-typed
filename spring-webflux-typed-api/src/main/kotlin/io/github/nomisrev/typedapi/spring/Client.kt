package io.github.nomisrev.typedapi.spring

import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.Request
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

suspend fun <A, B> WebClient.request(
    request: Request<A, B>,
    method: HttpMethod,
): WebClient.ResponseSpec {
    var currentUrl = request.path
    val builder = method(method)

    request.build { name, value, input ->
        when (input) {
            is Input.Body<*> -> value?.let {
                builder.bodyValue(value)
            }

            is Input.Path<*> -> currentUrl = currentUrl.replace("{${name}}", value.toString())
            is Input.Header<*> -> builder.header(name, value?.toString())
            is Input.Query<*> -> value?.let {
                builder.uri { uriBuilder ->
                    uriBuilder.queryParam(name, value.toString()).build()
                }
            }
        }
    }

    return builder.uri(currentUrl).retrieve()
}

suspend fun <A : Any, B : Any> WebClient.GET(request: Request<A, B>): WebClient.ResponseSpec =
    request(request, HttpMethod.GET)

suspend fun <A : Any, B : Any> WebClient.POST(request: Request<A, B>): WebClient.ResponseSpec =
    request(request, HttpMethod.POST)

suspend fun <A : Any, B : Any> WebClient.put(request: Request<A, B>): WebClient.ResponseSpec =
    request(request, HttpMethod.PUT)

suspend fun <A : Any, B : Any> WebClient.delete(request: Request<A, B>): WebClient.ResponseSpec =
    request(request, HttpMethod.DELETE)

suspend fun <A : Any, B : Any> WebClient.patch(request: Request<A, B>): WebClient.ResponseSpec =
    request(request, HttpMethod.PATCH)