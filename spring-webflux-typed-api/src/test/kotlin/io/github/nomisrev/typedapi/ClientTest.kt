package io.github.nomisrev.typedapi

import io.github.nomisrev.typedapi.spring.get
import io.github.nomisrev.typedapi.spring.route
import kotlinx.serialization.Serializable
import org.springframework.http.HttpMethod
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.RouterFunctionDsl
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import kotlin.test.Test
import kotlin.test.assertEquals

@Endpoint(path = "/client-test/{id}")
class SimpleClientApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
    val header: String by api.header<String>()
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
class NullableParamsApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String? by api.query<String?>()
    val header: String? by api.header<String?>()
}

inline fun <reified A : Any> RouterFunctionDsl.GET(
    noinline endpoint: (EndpointAPI) -> A,
    noinline block: suspend ServerRequest.(A) -> Mono<ServerResponse>,
) = add(route(A::class, HttpMethod.GET, endpoint, block))

class ClientTest {

    @Test
    fun testClientRequestBuilding() {
        val router = router {
            GET(::SimpleClientApi) { api ->
                val value = ClientTestResponse(id = api.id, name = api.name, header = api.header)
                ServerResponse.ok().bodyValue(value)
            }
        }

        val client = WebTestClient.bindToRouterFunction(router).build()

        client.get()
            .uri("/client-test/123?name=test-name")
            .header("header", "test-header")
            .exchange()
            .expectStatus().isOk
            .expectBody(ClientTestResponse::class.java)
            .value { response ->
                assertEquals(123, response.id)
                assertEquals("test-name", response.name)
                assertEquals("test-header", response.header)
            }
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
            add(get(::NullableParamsApi) { api ->
                ServerResponse.ok().bodyValue(
                    NullableResponse(
                        id = api.id,
                        name = api.name,
                        header = api.header
                    )
                )
            })
        }

        val client = WebTestClient.bindToRouterFunction(router).build()

        // Test with null values
        client.get()
            .uri("/nullable-test/123")
            .exchange()
            .expectStatus().isOk
            .expectBody(NullableResponse::class.java)
            .value { response ->
                assertEquals(123, response.id)
                assertEquals(null, response.name)
                assertEquals(null, response.header)
            }

        // Test with non-null values
        client.get()
            .uri("/nullable-test/456?name=test-name")
            .header("header", "test-header")
            .exchange()
            .expectStatus().isOk
            .expectBody(NullableResponse::class.java)
            .value { response ->
                assertEquals(456, response.id)
                assertEquals("test-name", response.name)
                assertEquals("test-header", response.header)
            }
    }
}