package io.ktor.route.fastapi

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

@Endpoint
class ServerTestApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
}

@Serializable
data class ServerTestParams(
    val id: Int,
    val name: String
) {
    fun request(): Request<ServerTestParams, ServerTestApi> = 
        Request(this, ::ServerTestApi)
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
        @Endpoint
        class Route1Api(api: EndpointAPI) {
            val id: Int by api.path<Int>()
        }
        
        @Endpoint
        class Route2Api(api: EndpointAPI) {
            val name: String by api.query<String>()
        }
        
        @Serializable
        data class Route1Params(val id: Int) {
            fun request(): Request<Route1Params, Route1Api> = 
                Request(this, ::Route1Api)
        }
        
        @Serializable
        data class Route2Params(val name: String) {
            fun request(): Request<Route2Params, Route2Api> = 
                Request(this, ::Route2Api)
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
        
        // Test route1
        val route1Params = Route1Params(123)
        val route1Response = client.get("/route1/{id}", route1Params.request())
            .body<RouteResponse>()
        
        assertEquals("route1", route1Response.route)
        assertEquals("123", route1Response.value)
        
        // Test route2
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
        
        @Endpoint
        class GetApi(api: EndpointAPI) {
            val id: Int by api.path<Int>()
        }
        
        @Endpoint
        class PostApi(api: EndpointAPI) {
            val id: Int by api.path<Int>()
            val body: ServerTestBody by api.body<ServerTestBody>()
        }
        
        @Serializable
        data class GetParams(val id: Int) {
            fun request(): Request<GetParams, GetApi> = 
                Request(this, ::GetApi)
        }
        
        @Serializable
        data class PostParams(val id: Int, val body: ServerTestBody) {
            fun request(): Request<PostParams, PostApi> = 
                Request(this, ::PostApi)
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
        
        // Test GET
        val getParams = GetParams(123)
        val getResponse = client.get("/method-test/{id}", getParams.request())
            .body<MethodResponse>()
        
        assertEquals("GET", getResponse.method)
        assertEquals(123, getResponse.id)
        
        // Test POST
        val postBody = ServerTestBody("test-value")
        val postParams = PostParams(456, postBody)
        val postResponse = client.post("/method-test/{id}", postParams.request())
            .body<MethodResponse>()
        
        assertEquals("POST", postResponse.method)
        assertEquals(456, postResponse.id)
        assertEquals(postBody, postResponse.body)
    }
}