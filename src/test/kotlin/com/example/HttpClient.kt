package com.example

import com.example.codec.Codec
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.cookie
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.appendEncodedPathSegments

// TODO move to separate module

suspend fun <Input> HttpClient.get(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Get, route, input)

suspend fun <Input> HttpClient.post(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Post, route, input)

suspend fun <Input> HttpClient.put(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Put, route, input)

suspend fun <Input> HttpClient.patch(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Patch, route, input)

suspend fun <Input> HttpClient.delete(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Delete, route, input)

suspend fun <Input> HttpClient.head(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Head, route, input)

suspend fun <Input> HttpClient.options(
    route: Route<Input, Unit>,
    input: Input
): HttpResponse = request(HttpMethod.Options, route, input)

suspend fun <Input> HttpClient.request(
    method: HttpMethod,
    route: Route<Input, Unit>,
    input: Input
): HttpResponse =
    request(HttpRequestBuilder().apply {
        this.method = method
        val input = route.reverse(input)
        val params = when {
            input === Unit -> emptyArray()
            input !is Params ->
                if (route.arity == 1) arrayOf(input) else throw TODO("Not supporting transformation of Params yet.")

            else -> input.toArray().also {
                require(it.size == route.arity) { "Expected ${route.arity} parameters, got ${it.size}" }
            }
        }
        var index = 0
        val segments = route.path.segments.mapIndexedNotNull { idx, (path, parameter) ->
            if (parameter == null) path else {
                val codec = parameter.codec as Codec<Any?>
                val param = params[index++]
                val value = codec.serialize(param)
                if (value == null && idx == route.path.segments.lastIndex) null else value
            }
        }
        url.appendEncodedPathSegments(segments)

        route.parameters.zip(params.drop(index)) { parameter, value ->
            when (parameter) {
                // TODO We should support all cookie parameters
                is Parameter.Cookie -> this.cookie(parameter.name, value as String)
                is Parameter.Header ->
                    when {
                        value == null ->
                            if (parameter.optional) Unit
                            else throw IllegalStateException("Header ${parameter.name} is required")

                        else -> headers.append(parameter.name, value as String)
                    }
// TODO https://youtrack.jetbrains.com/issue/KTOR-7824/Ktor-doesnt-parse-multiple-headers
//                is Parameter.Headers ->
//                    @Suppress("UNCHECKED_CAST")
//                    headers.appendAll(parameter.name, value as List<String>)

                is Parameter.Query<*> -> parameter(parameter.name, value)
            }
        }
    })
