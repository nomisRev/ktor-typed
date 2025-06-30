package io.github.nomisrev.typedapi

import io.ktor.http.HttpMethod
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.github.nomisrev.typedapi.Request
import io.github.nomisrev.typedapi.properties
import io.github.nomisrev.typedapi.ktor.get
import io.github.nomisrev.typedapi.ktor.post
import io.github.nomisrev.typedapi.ktor.route
import kotlinx.serialization.Serializable
import kotlin.reflect.full.memberProperties
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerTestApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
}

@Serializable
data class ServerTestParams(
    val id: Int,
    val name: String
) {
    fun request(): Request<ServerTestParams, ServerTestApi> {
        val props = properties(
            ServerTestParams::id,
            ServerTestParams::name
        )
        return Request(this, ::ServerTestApi, props)
    }
}

@Serializable
data class ServerTestResponse(
    val id: Int,
    val name: String,
    val message: String
)

class ServerTest {
    
    @Test
    fun testBasicRouteHandling() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            
            route("/server-test/{id}", HttpMethod.Get, ::ServerTestApi) { api ->
                call.respond(ServerTestResponse(
                    id = api.id,
                    name = api.name,
                    message = "Success"
                ))
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        val params = ServerTestParams(123, "test-name")
        val response = client.get("/server-test/{id}", params.request())
            .body<ServerTestResponse>()
        
        assertEquals(123, response.id)
        assertEquals("test-name", response.name)
        assertEquals("Success", response.message)
    }
    
    @Test
    fun testMultipleRoutes() = testApplication {
        class Route1Api(api: EndpointAPI) {
            val id: Int by api.path<Int>()
        }
        
        class Route2Api(api: EndpointAPI) {
            val name: String by api.query<String>()
        }
        
        @Serializable
        data class Route1Params(val id: Int) {
            fun request(): Request<Route1Params, Route1Api> {
                val props = properties(Route1Params::id)
                return Request(this, ::Route1Api, props)
            }
        }
        
        @Serializable
        data class Route2Params(val name: String) {
            fun request(): Request<Route2Params, Route2Api> {
                val props = properties(Route2Params::name)
                return Request(this, ::Route2Api, props)
            }
        }
        
        @Serializable
        data class RouteResponse(val route: String, val value: String)
        
        routing {
            install(ContentNegotiation) { json() }
            
            route("/route1/{id}", HttpMethod.Get, ::Route1Api) { api ->
                call.respond(RouteResponse("route1", api.id.toString()))
            }
            
            route("/route2", HttpMethod.Get, ::Route2Api) { api ->
                call.respond(RouteResponse("route2", api.name))
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        // io.github.nomisrev.typedapi.Test route1
        val route1Params = Route1Params(123)
        val route1Response = client.get("/route1/{id}", route1Params.request())
            .body<RouteResponse>()
        
        assertEquals("route1", route1Response.route)
        assertEquals("123", route1Response.value)
        
        // io.github.nomisrev.typedapi.Test route2
        val route2Params = Route2Params("test-name")
        val route2Response = client.get("/route2", route2Params.request())
            .body<RouteResponse>()
        
        assertEquals("route2", route2Response.route)
        assertEquals("test-name", route2Response.value)
    }
    
    @Test
    fun testDifferentHttpMethods() = testApplication {
        @Serializable
        data class ServerTestBody(val value: String)
        
        class GetApi(api: EndpointAPI) {
            val id: Int by api.path<Int>()
        }
        
        class PostApi(api: EndpointAPI) {
            val id: Int by api.path<Int>()
            val body: ServerTestBody by api.body<ServerTestBody>()
        }
        
        @Serializable
        data class GetParams(val id: Int) {
            fun request(): Request<GetParams, GetApi> =
                Request(this, ::GetApi, properties(GetParams::id))
        }
        
        @Serializable
        data class PostParams(val id: Int, val body: ServerTestBody) {
            fun request(): Request<PostParams, PostApi> =
                Request(this, ::PostApi, properties(PostParams::id, PostParams::body))
        }
        
        @Serializable
        data class MethodResponse(val method: String, val id: Int, val body: ServerTestBody? = null)
        
        routing {
            install(ContentNegotiation) { json() }
            
            route("/method-test/{id}", HttpMethod.Get, ::GetApi) { api ->
                call.respond(MethodResponse("GET", api.id))
            }
            
            route("/method-test/{id}", HttpMethod.Post, ::PostApi) { api ->
                call.respond(MethodResponse("POST", api.id, api.body))
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        // io.github.nomisrev.typedapi.Test GET
        val getParams = GetParams(123)
        val getResponse = client.get("/method-test/{id}", getParams.request())
            .body<MethodResponse>()
        
        assertEquals("GET", getResponse.method)
        assertEquals(123, getResponse.id)
        
        // io.github.nomisrev.typedapi.Test POST
        val postBody = ServerTestBody("test-value")
        val postParams = PostParams(456, postBody)
        val postResponse = client.post("/method-test/{id}", postParams.request())
            .body<MethodResponse>()
        
        assertEquals("POST", postResponse.method)
        assertEquals(456, postResponse.id)
        assertEquals(postBody, postResponse.body)
    }
}