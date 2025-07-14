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
class ProfileApi(api: EndpointAPI) : HttpRequestValue {
    val profileId: Int by api.path<Int>()
    val name by api.query<String>()
    val email by api.query<String?>()
    val userAgent by api.header<String>()

    // Compiler plugin generates everything below
    constructor(profileId: Int, name: String, email: String?, userAgent: String) : this(
        MapEndpointAPI(
            mapOf(
                "profileId" to profileId,
                "name" to name,
                "email" to email,
                "userAgent" to userAgent
            )
        )
    )

    override fun path(): String = "/profile/$profileId"

    override fun query(block: (Any?, Input.Query<Any?>) -> Unit) {
        block(name, Query<String>("name") as Input.Query<Any?>)
        block(email, Query<String?>("email") as Input.Query<Any?>)
    }

    override fun path(block: (Any?, Input.Path<Any?>) -> Unit) {
        block(profileId, Path<Int>("profileId") as Input.Path<Any?>)
    }

    override fun header(block: (Any?, Input.Header<Any?>) -> Unit) {
        block(userAgent, Header<String>("userAgent") as Input.Header<Any?>)
    }

    override fun body(block: (Any?, Input.Body<Any?>) -> Unit) {}
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
