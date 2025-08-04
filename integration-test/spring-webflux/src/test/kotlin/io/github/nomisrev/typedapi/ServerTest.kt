package io.github.nomisrev.typedapi

import io.github.nomisrev.typedapi.spring.GET
import io.github.nomisrev.typedapi.spring.POST
import kotlinx.serialization.Serializable
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import kotlin.test.Test

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
            GET(SimpleTestApi) { api ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(ServerTestResponse(id = api.id, name = api.name, message = "Success"))
            }
        }

        val client = bindToRouterFunction(router).build()

        client.get()
            .uri("/server-test/123?name=test-name")
            .exchange()
            .expectStatus().isOk
            .expectBody(ServerTestResponse::class.java)
            .isEqualTo(ServerTestResponse(id = 123, name = "test-name", message = "Success"))
    }

    @Test
    fun testMultipleRoutes() {
        @Serializable
        data class RouteResponse(val route: String, val value: String)

        val router = router {
            GET(Route1Api) { api ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(RouteResponse("route1", api.id.toString()))
            }

            GET(Route2Api) { api ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(RouteResponse("route2", api.name))
            }
        }

        val client = bindToRouterFunction(router).build()

        // Test route1
        client.get()
            .uri("/route1/123")
            .exchange()
            .expectStatus().isOk
            .expectBody(RouteResponse::class.java)
            .isEqualTo(RouteResponse("route1", "123"))

        client.get()
            .uri("/route2?name=test-name")
            .exchange()
            .expectStatus().isOk
            .expectBody(RouteResponse::class.java)
            .isEqualTo(RouteResponse("route2", "test-name"))
    }

    @Test
    fun testDifferentHttpMethods() {
        @Serializable
        data class MethodResponse(val method: String, val id: Int, val body: ServerTestBody? = null)

        val router = router {
            GET(GetApi) { api ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(MethodResponse("GET", api.id))
            }

            POST(PostApi) { api ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(MethodResponse("POST", api.id, api.body))
            }
        }

        val client = bindToRouterFunction(router).build()

        client.get()
            .uri("/method-test/123")
            .exchange()
            .expectStatus().isOk
            .expectBody(MethodResponse::class.java)
            .isEqualTo(MethodResponse("GET", 123))

        val postBody = ServerTestBody("test-value")
        client.post()
            .uri("/method-test/456")
            .bodyValue(postBody)
            .exchange()
            .expectStatus().isOk
            .expectBody(MethodResponse::class.java)
            .isEqualTo(MethodResponse("POST", 456, postBody))
    }
}