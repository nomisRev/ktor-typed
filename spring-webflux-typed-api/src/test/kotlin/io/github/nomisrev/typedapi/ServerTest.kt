package io.github.nomisrev.typedapi

import io.github.nomisrev.typedapi.spring.get
import io.github.nomisrev.typedapi.spring.post
import kotlinx.serialization.Serializable
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import kotlin.test.Test
import kotlin.test.assertEquals

@Endpoint(path = "/server-test/{id}")
class SimpleTestApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
}

@Serializable
data class ServerTestResponse(
    val id: Int,
    val name: String,
    val message: String
)

@Endpoint(path = "/route1/{id}")
class Route1Api(api: EndpointAPI) {
    val id: Int by api.path<Int>()
}

@Endpoint(path = "/route2")
class Route2Api(api: EndpointAPI) {
    val name: String by api.query<String>()
}

@Endpoint(path = "/method-test/{id}")
class GetApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
}

@Endpoint(path = "/method-test/{id}")
class PostApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val body: ServerTestBody by api.body<ServerTestBody>()
}

@Serializable
data class ServerTestBody(val value: String)

class ServerTest {

    @Test
    fun testBasicRouteHandling() {
        val router = router {
            add(get(::SimpleTestApi) { api ->
                ServerResponse.ok().bodyValue(
                    ServerTestResponse(
                        id = api.id,
                        name = api.name,
                        message = "Success"
                    )
                )
            })
        }

        val client = WebTestClient.bindToRouterFunction(router).build()

        client.get()
            .uri("/server-test/123?name=test-name")
            .exchange()
            .expectStatus().isOk
            .expectBody(ServerTestResponse::class.java)
            .value { response ->
                assertEquals(123, response.id)
                assertEquals("test-name", response.name)
                assertEquals("Success", response.message)
            }
    }

    @Test
    fun testMultipleRoutes() {
        @Serializable
        data class RouteResponse(val route: String, val value: String)

        val router = router {
            add(get(::Route1Api) { api ->
                ServerResponse.ok().bodyValue(RouteResponse("route1", api.id.toString()))
            })

            add(get(::Route2Api) { api ->
                ServerResponse.ok().bodyValue(RouteResponse("route2", api.name))
            })
        }

        val client = WebTestClient.bindToRouterFunction(router).build()

        // Test route1
        client.get()
            .uri("/route1/123")
            .exchange()
            .expectStatus().isOk
            .expectBody(RouteResponse::class.java)
            .value { response ->
                assertEquals("route1", response.route)
                assertEquals("123", response.value)
            }

        // Test route2
        client.get()
            .uri("/route2?name=test-name")
            .exchange()
            .expectStatus().isOk
            .expectBody(RouteResponse::class.java)
            .value { response ->
                assertEquals("route2", response.route)
                assertEquals("test-name", response.value)
            }
    }

    @Test
    fun testDifferentHttpMethods() {
        @Serializable
        data class MethodResponse(val method: String, val id: Int, val body: ServerTestBody? = null)

        val router = router {
            add(get(::GetApi) { api ->
                ServerResponse.ok().bodyValue(MethodResponse("GET", api.id))
            })

            add(post(::PostApi) { api ->
                ServerResponse.ok().bodyValue(MethodResponse("POST", api.id, api.body))
            })
        }

        val client = WebTestClient.bindToRouterFunction(router).build()

        // Test GET
        client.get()
            .uri("/method-test/123")
            .exchange()
            .expectStatus().isOk
            .expectBody(MethodResponse::class.java)
            .value { response ->
                assertEquals("GET", response.method)
                assertEquals(123, response.id)
            }

        // Test POST
        val postBody = ServerTestBody("test-value")
        client.post()
            .uri("/method-test/456")
            .bodyValue(postBody)
            .exchange()
            .expectStatus().isOk
            .expectBody(MethodResponse::class.java)
            .value { response ->
                assertEquals("POST", response.method)
                assertEquals(456, response.id)
                assertEquals(postBody, response.body)
            }
    }
}