package io.github.nomisrev.typedapi

import io.ktor.http.HttpMethod
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.github.nomisrev.typedapi.ktor.get
import io.github.nomisrev.typedapi.ktor.post
import io.github.nomisrev.typedapi.ktor.route
import kotlinx.serialization.Serializable
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
    fun testBasicRouteHandling() = testApplication {
        routing {
            install(ContentNegotiation) { json() }

            get(SimpleTestApi) { api ->
                call.respond(
                    ServerTestResponse(
                        id = api.id,
                        name = api.name,
                        message = "Success"
                    )
                )
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val params = SimpleTestApi(123, "test-name")
        val response = client.get(params).body<ServerTestResponse>()

        assertEquals(
            ServerTestResponse(123, "test-name", "Success"),
            response
        )
    }

    @Test
    fun testMultipleRoutes() = testApplication {
        @Serializable
        data class RouteResponse(val route: String, val value: String)

        routing {
            install(ContentNegotiation) { json() }

            get(Route1Api) { api ->
                call.respond(RouteResponse("route1", api.id.toString()))
            }

            get(Route2Api) { api ->
                call.respond(RouteResponse("route2", api.name))
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        // io.github.nomisrev.typedapi.Test route1
        val route1Params = Route1Api(123)
        val route1Response = client.get(route1Params)
            .body<RouteResponse>()

        assertEquals(
            RouteResponse("route1", "123"),
            route1Response
        )

        // io.github.nomisrev.typedapi.Test route2
        val route2Params = Route2Api("test-name")
        val route2Response = client.get(route2Params)
            .body<RouteResponse>()

        assertEquals(
            RouteResponse("route2", "test-name"),
            route2Response
        )
    }

    @Test
    fun testDifferentHttpMethods() = testApplication {
        @Serializable
        data class MethodResponse(val method: String, val id: Int, val body: ServerTestBody? = null)

        routing {
            install(ContentNegotiation) { json() }

            get(GetApi) { api ->
                call.respond(MethodResponse("GET", api.id))
            }

            post(PostApi) { api ->
                call.respond(MethodResponse("POST", api.id, api.body))
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        // io.github.nomisrev.typedapi.Test GET
        val getParams = GetApi(123)
        val getResponse = client.get(getParams)
            .body<MethodResponse>()

        assertEquals(
            MethodResponse("GET", 123),
            getResponse
        )

        // io.github.nomisrev.typedapi.Test POST
        val postBody = ServerTestBody("test-value")
        val postParams = PostApi(456, postBody)
        val postResponse = client.post(postParams).body<MethodResponse>()

        assertEquals(
            MethodResponse("POST", 456, postBody),
            postResponse
        )
    }
}