package io.github.nomisrev.typedapi

import io.github.nomisrev.typedapi.spring.GET
import kotlinx.serialization.Serializable
import org.springframework.http.HttpMethod
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
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

@Serializable
data class Profile(val id: Int, val name: String, val email: String?, val userAgent: String)

class Test {
    @Test
    fun example() {
        val router = router {
            GET(ProfileApi) { api ->
                ServerResponse.ok().bodyValue(Profile(api.profileId, api.name, api.email, api.userAgent))
            }
        }

        val client = WebTestClient.bindToRouterFunction(router).build()

        client.GET(ProfileApi(1, "name", null, "userAgent"))
            .expectStatus().isOk
            .expectBody<Profile>()
            .isEqualTo(Profile(1, "name", null, "userAgent"))
    }
}

fun <A : HttpRequestValue> WebTestClient.request(method: HttpMethod, value: A): WebTestClient.ResponseSpec {
    var currentUrl = value.path()
    value.path { value, input ->
        currentUrl = currentUrl.replace("{${input.name}}", value.toString())
    }
    val uri = currentUrl
    val bodySpec = method(method).uri { uriBuilder ->
        uriBuilder.path(uri)
        value.query { value, input ->
            if (value != null) {
                uriBuilder.queryParam(input.name, value.toString()).build()
            }
        }
        uriBuilder.build()
    }

    var headersSpec: WebTestClient.RequestHeadersSpec<*> = bodySpec
    value.body { value, _ ->
        if (value != null) {
            headersSpec = bodySpec.bodyValue(value)
        }
    }
    value.header { value, input ->
        if (value != null) {
            headersSpec.header(input.casing(input.name!!), value.toString())
        }
    }
    return headersSpec.exchange()
}

fun <A : HttpRequestValue> WebTestClient.GET(value: A): WebTestClient.ResponseSpec =
    request(HttpMethod.GET, value)

fun <A : HttpRequestValue> WebTestClient.POST(
    value: A
): WebTestClient.ResponseSpec =
    request(HttpMethod.POST, value)

fun <A : HttpRequestValue> WebTestClient.PUT(value: A): WebTestClient.ResponseSpec =
    request(HttpMethod.PUT, value)

fun <A : HttpRequestValue> WebTestClient.DELETE(value: A): WebTestClient.ResponseSpec =
    request(HttpMethod.DELETE, value)

fun <A : HttpRequestValue> WebTestClient.PATCH(value: A): WebTestClient.ResponseSpec =
    request(HttpMethod.PATCH, value)
