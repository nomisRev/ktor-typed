package io.github.nomisrev.typedapi

import io.github.nomisrev.typedapi.spring.route
import kotlinx.serialization.Serializable
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import kotlin.test.Test

@Endpoint(path = "/client-test/{id}")
class SimpleClientApi(api: EndpointAPI) : HttpRequestValue {

    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
    val header: String by api.header<String>()

    // Compiler plugin generates everything below
    constructor(id: Int, name: String, header: String) : this(
        MapEndpointAPI(
            mapOf(
                "id" to id,
                "name" to name,
                "header" to header
            )
        )
    )

    override fun path(): String = "/client-test/$id"

    override fun query(block: (Any?, Input.Query<Any?>) -> Unit) {
        block(name, Query<String>() as Input.Query<Any?>)
    }

    override fun path(block: (Any?, Input.Path<Any?>) -> Unit) {
        block(id, Path<String>() as Input.Path<Any?>)
    }

    override fun header(block: (Any?, Input.Header<Any?>) -> Unit) {
        block(header, Header<String>() as Input.Header<Any?>)
    }

    override fun body(block: (Any?, Input.Body<Any?>) -> Unit) {}
}

@Serializable
data class ClientTestBody(val value: String)

@Serializable
data class ClientTestResponse(
    val id: Int,
    val name: String,
    val header: String
)

@Endpoint(path = "/nullable-test/{id}")
class NullableParamsApi(api: EndpointAPI) : HttpRequestValue {
    val id: Int by api.path<Int>()
    val name: String? by api.query<String?>()
    val header: String? by api.header<String?>()

    // Compiler plugin generates everything below
    constructor(id: Int, name: String?, header: String?) : this(
        MapEndpointAPI(
            mapOf(
                "id" to id,
                "name" to name,
                "header" to header
            )
        )
    )
    override fun path(): String = "/nullable-test/$id"

    override fun query(block: (Any?, Input.Query<Any?>) -> Unit) {
        block(name, Query<String>() as Input.Query<Any?>)
    }

    override fun path(block: (Any?, Input.Path<Any?>) -> Unit) {
        block(id, Path<String>() as Input.Path<Any?>)
    }

    override fun header(block: (Any?, Input.Header<Any?>) -> Unit) {
        block(header, Header<String>() as Input.Header<Any?>)
    }

    override fun body(block: (Any?, Input.Body<Any?>) -> Unit) {}
}

class ClientTest {

    @Test
    fun testClientRequestBuilding() {
        // Create a simple controller function that returns a hardcoded response
        val router = router {
            GET("/client-test/{id}") { request ->
                val id = request.pathVariable("id").toInt()
                val name = request.queryParam("name").orElse("")
                val header = request.headers().header("header").firstOrNull() ?: ""

                val value = ClientTestResponse(id = id, name = name, header = header)

                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(value)
            }
        }

        val client = WebTestClient.bindToRouterFunction(router).build()

        client.get()
            .uri("/client-test/123?name=test-name")
            .header("header", "test-header")
            .exchange()
            .expectStatus().isOk
            .expectBody<ClientTestResponse>()
            .isEqualTo(ClientTestResponse(id = 123, name = "test-name", header = "test-header"))
    }

    @Test
    fun testClientRequestWithNullableParameters() {
        @Serializable
        data class NullableResponse(
            val id: Int,
            val name: String?,
            val header: String?
        )

        val router = router {
            GET("/nullable-test/{id}") { request ->
                val id = request.pathVariable("id").toInt()
                val name = request.queryParam("name").orElse(null)
                val header = request.headers().header("header").firstOrNull()

                val response = NullableResponse(
                    id = id,
                    name = name,
                    header = header
                )

                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(response)
            }
        }

        val client = WebTestClient.bindToRouterFunction(router).build()

        client.get()
            .uri("/nullable-test/123")
            .exchange()
            .expectStatus().isOk
            .expectBody<NullableResponse>()
            .isEqualTo(NullableResponse(id = 123, name = null, header = null))

        client.get()
            .uri("/nullable-test/456?name=test-name")
            .header("header", "test-header")
            .exchange()
            .expectStatus().isOk
            .expectBody<NullableResponse>()
            .isEqualTo(NullableResponse(id = 456, name = "test-name", header = "test-header"))
    }
}