package io.github.nomisrev.typedapi.ktor

import io.github.nomisrev.typedapi.HttpRequestValue
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

suspend fun <A : HttpRequestValue> HttpClient.request(
    method: HttpMethod,
    value: A,
): HttpResponse = request {
    url.takeFrom(value.path())
    this.method = method
    value.header { value, input -> header(input.casing(input.name!!), value) }
    value.query { value, input -> parameter(input.name!!, value) }
    value.body { value, input ->
        // TODO parameterise, or collect from ContentNegotiation
        contentType(ContentType.Application.Json)
        setBody(value)
    }
}

suspend fun <A : HttpRequestValue> HttpClient.get(value: A): HttpResponse = request(HttpMethod.Get, value)
suspend fun <A : HttpRequestValue> HttpClient.post(value: A): HttpResponse = request(HttpMethod.Post, value)
suspend fun <A : HttpRequestValue> HttpClient.put(value: A): HttpResponse = request(HttpMethod.Put, value)
suspend fun <A : HttpRequestValue> HttpClient.delete(value: A): HttpResponse = request(HttpMethod.Delete, value)
suspend fun <A : HttpRequestValue> HttpClient.patch(value: A): HttpResponse = request(HttpMethod.Patch, value)
suspend fun <A : HttpRequestValue> HttpClient.options(value: A): HttpResponse = request(HttpMethod.Options, value)
suspend fun <A : HttpRequestValue> HttpClient.head(value: A): HttpResponse = request(HttpMethod.Head, value)
