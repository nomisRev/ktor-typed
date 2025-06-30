package io.github.nomisrev.typedapi.ktor

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.takeFrom
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.Request

suspend fun <A, B> HttpClient.request(
    request: Request<A, B>,
    path: String,
    method: HttpMethod,
): HttpResponse = request {
    var currentUrl = path
    request.build { name, value, input ->
        when (input) {
            // TODO: How to parameterise ContentType.
            is Input.Body<*> -> value?.let {
                contentType(ContentType.Application.Json)
                setBody(value)
            }

            is Input.Path<*> -> currentUrl = currentUrl.replace("{${name}}", value.toString())
            is Input.Header<*> -> header(name, value)
            is Input.Query<*> -> parameter(name, value)
        }
    }
    url.takeFrom(currentUrl)
    this.method = method
}

suspend fun <A : Any, B : Any> HttpClient.get(path: String, request: Request<A, B>): HttpResponse =
    request(request, path, HttpMethod.Get)

suspend fun <A : Any, B : Any> HttpClient.post(path: String, request: Request<A, B>): HttpResponse =
    request(request, path, HttpMethod.Post)

// TODO Add missing overloads