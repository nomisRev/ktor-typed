package io.github.nomisrev.typedapi.spring

import io.github.nomisrev.typedapi.HttpRequestValue
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange

suspend fun <A : HttpRequestValue, T : Any> WebClient.request(
    method: HttpMethod,
    value: A,
    responseHandler: suspend (ClientResponse) -> T
): T {
    val uri = value.path()
    val bodySpec = method(method).uri { uriBuilder ->
        uriBuilder.path(uri)
        value.query { value, input ->
            if (value != null) uriBuilder.queryParam(input.name, value.toString()).build()
        }
        uriBuilder.build()
    }

    var headersSpec: WebClient.RequestHeadersSpec<*>? = null
    value.body { value, _ ->
        if (value != null) {
            headersSpec = bodySpec.bodyValue(value)
        } else {
            headersSpec = bodySpec
        }
    }
    value.header { value, input ->
        headersSpec?.header(input.casing(input.name!!), value?.toString())
    }
    requireNotNull(headersSpec) { "Cannot be null" }
    return headersSpec.awaitExchange(responseHandler)
}

suspend fun <A : HttpRequestValue, T : Any> WebClient.GET(value: A, responseHandler: suspend (ClientResponse) -> T): T =
    request(HttpMethod.GET, value, responseHandler)

suspend fun <A : HttpRequestValue, T : Any> WebClient.POST(
    value: A,
    responseHandler: suspend (ClientResponse) -> T
): T =
    request(HttpMethod.POST, value, responseHandler)

suspend fun <A : HttpRequestValue, T : Any> WebClient.PUT(value: A, responseHandler: suspend (ClientResponse) -> T): T =
    request(HttpMethod.PUT, value, responseHandler)

suspend fun <A : HttpRequestValue, T : Any> WebClient.DELETE(
    value: A,
    responseHandler: suspend (ClientResponse) -> T
): T =
    request(HttpMethod.DELETE, value, responseHandler)

suspend fun <A : HttpRequestValue, T : Any> WebClient.PATCH(
    value: A,
    responseHandler: suspend (ClientResponse) -> T
): T =
    request(HttpMethod.PATCH, value, responseHandler)
