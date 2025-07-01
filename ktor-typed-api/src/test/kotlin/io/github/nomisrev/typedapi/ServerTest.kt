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

            get( ::SimpleTestApi) { api ->
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

        val params = SimpleTest(123, "test-name")
        val response = client.get(params.request())
            .body<ServerTestResponse>()

        assertEquals(123, response.id)
        assertEquals("test-name", response.name)
        assertEquals("Success", response.message)
    }

    @Test
    fun testMultipleRoutes() = testApplication {
        @Serializable
        data class RouteResponse(val route: String, val value: String)

        routing {
            install(ContentNegotiation) { json() }

            get( ::Route1Api) { api ->
                call.respond(RouteResponse("route1", api.id.toString()))
            }

            get( ::Route2Api) { api ->
                call.respond(RouteResponse("route2", api.name))
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        // io.github.nomisrev.typedapi.Test route1
        val route1Params = Route1(123)
        val route1Response = client.get(route1Params.request())
            .body<RouteResponse>()

        assertEquals("route1", route1Response.route)
        assertEquals("123", route1Response.value)

        // io.github.nomisrev.typedapi.Test route2
        val route2Params = Route2("test-name")
        val route2Response = client.get(route2Params.request())
            .body<RouteResponse>()

        assertEquals("route2", route2Response.route)
        assertEquals("test-name", route2Response.value)
    }

    @Test
    fun testDifferentHttpMethods() = testApplication {
        @Serializable
        data class MethodResponse(val method: String, val id: Int, val body: ServerTestBody? = null)

        routing {
            install(ContentNegotiation) { json() }

            get( ::GetApi) { api ->
                call.respond(MethodResponse("GET", api.id))
            }

            post( ::PostApi) { api ->
                call.respond(MethodResponse("POST", api.id, api.body))
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        // io.github.nomisrev.typedapi.Test GET
        val getParams = Get(123)
        val getResponse = client.get(getParams.request())
            .body<MethodResponse>()

        assertEquals("GET", getResponse.method)
        assertEquals(123, getResponse.id)

        // io.github.nomisrev.typedapi.Test POST
        val postBody = ServerTestBody("test-value")
        val postParams = Post(456, postBody)
        val postResponse = client.post(postParams.request())
            .body<MethodResponse>()

        assertEquals("POST", postResponse.method)
        assertEquals(456, postResponse.id)
        assertEquals(postBody, postResponse.body)
    }
}