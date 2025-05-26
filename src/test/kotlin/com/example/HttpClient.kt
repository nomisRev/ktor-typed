package com.example

import com.example.codec.Codec
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.cookie
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Cookie
import io.ktor.http.HttpMethod
import io.ktor.http.appendEncodedPathSegments

// TODO move to separate module

suspend fun <Input : Any> HttpClient.get(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Get, route, input)

suspend fun <Input : Any> HttpClient.post(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Post, route, input)

suspend fun <Input : Any> HttpClient.put(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Put, route, input)

suspend fun <Input : Any> HttpClient.patch(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Patch, route, input)

suspend fun <Input : Any> HttpClient.delete(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Delete, route, input)

suspend fun <Input : Any> HttpClient.head(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Head, route, input)

suspend fun <Input : Any> HttpClient.options(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Options, route, input)

suspend fun <Input : Any> HttpClient.request(
    method: HttpMethod,
    route: Route<Input, Unit>,
    input: Input
): HttpResponse =
    request(HttpRequestBuilder().apply {
        this.method = method
        val input = route.reverse(input)
        if (input !is Params) throw TODO("Not supporting transformation of Params yet.")
        val params = input.toList()
        if (params.size != route.arity) throw IllegalStateException("Expected ${route.arity} parameters, got ${params.size}")
        var index = 0
        val segments = route.path.segments.mapIndexedNotNull { idx, (path, parameter) ->
            if (parameter == null) path else {
                val codec = parameter.deserialize as Codec<Any?>
                val value = codec.serialize(params[index++])
                if (value == null && idx == route.path.segments.lastIndex) null else value
            }
        }
        url.appendEncodedPathSegments(segments)

        route.parameters.zip(params.drop(index)) { parameter, value ->
            val value = when (parameter) {
                is Parameter.Cookie -> (value as String).let { cookie ->
                    // TODO We should support all cookie parameters
                    this.cookie(parameter.name, value)
                }

                is Parameter.Header -> this.headers.appendAll(parameter.name, value as List<String>)
                is Parameter.Query<*> -> parameter(parameter.name, value as String)
            }
        }
    })