package io.github.nomisrev.typedapi

import io.github.nomisrev.typedapi.spring.GET
import io.github.nomisrev.typedapi.spring.request
import kotlinx.serialization.Serializable
import org.springframework.http.HttpMethod
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import kotlin.test.Test

@Endpoint(path = "/profile/{profileId}")
class ProfileApi(api: EndpointAPI) {
    val profileId: Int by api.path<Int>()
    val name by api.query<String>()
    val email by api.query<String?>()
    val userAgent by api.header<String>()
}

@Serializable
data class TestBody(val value: String)

class Test {
    @Test
    fun example() {
        val router = router {
            GET(::ProfileApi) { api ->
                ServerResponse.ok().bodyValue(Profile(api.profileId, api.name, api.email, api.userAgent))
            }
        }

        val client = WebTestClient.bindToRouterFunction(router).build()

        val request = Profile(1, "name", null, "userAgent").request()
        client.GET(request)
            .expectStatus().isOk
            .expectBody<Profile>()
            .isEqualTo(Profile(1, "name", null, "userAgent"))
    }
}

fun <A : Any, B : Any> WebTestClient.GET(request: Request<A, B>): WebTestClient.ResponseSpec =
    request(request, HttpMethod.GET)

fun <A : Any, B : Any> WebTestClient.POST(request: Request<A, B>): WebTestClient.ResponseSpec =
    request(request, HttpMethod.POST)

fun <A : Any, B : Any> WebTestClient.put(request: Request<A, B>): WebTestClient.ResponseSpec =
    request(request, HttpMethod.PUT)

fun <A : Any, B : Any> WebTestClient.delete(request: Request<A, B>): WebTestClient.ResponseSpec =
    request(request, HttpMethod.DELETE)

fun <A : Any, B : Any> WebTestClient.patch(request: Request<A, B>): WebTestClient.ResponseSpec =
    request(request, HttpMethod.PATCH)

fun <A, B> WebTestClient.request(
    request: Request<A, B>,
    method: HttpMethod,
): WebTestClient.ResponseSpec {
    // TODO clean-up this implementation, and optimise. Code can be super ugly...
    var currentUrl = request.path
    val builder = method(method)

    val queryParams = mutableMapOf<String, String?>()

    request.build { name, value, input ->
        when (input) {
            is Input.Path<*> -> currentUrl = currentUrl.replace("{${name}}", value.toString())
            is Input.Header<*> -> {
                val headerName = input.name ?: input.casing(name)
                builder.header(headerName, value?.toString())
            }

            is Input.Body<*> -> value?.let { builder.bodyValue(value) }
            is Input.Query<*> -> value?.let { queryParams[name] = value.toString() }
        }
    }

    return builder.uri { uriBuilder ->
        val uriBuilderWithPath = uriBuilder.path(currentUrl)
        queryParams.forEach { (name, value) ->
            if (value != null) {
                uriBuilderWithPath.queryParam(name, value)
            }
        }
        uriBuilderWithPath.build()
    }.exchange()
}