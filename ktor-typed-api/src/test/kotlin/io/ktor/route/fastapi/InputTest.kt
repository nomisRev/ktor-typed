package io.ktor.route.fastapi

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

// Extension function for POST requests
suspend fun <A : Any, B : Any> HttpClient.post(path: String, request: Request<A, B>): HttpResponse {
    return request {
        method = HttpMethod.Post
        ClientAPI(request, path, this).build()
    }
}

@Endpoint
class PathParamApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val optionalId: Int? by api.path<Int?>()
}

@Endpoint
class QueryParamApi(api: EndpointAPI) {
    val name: String by api.query<String>()
    val optionalName: String? by api.query<String?>()
}

@Endpoint
class HeaderParamApi(api: EndpointAPI) {
    val userAgent: String by api.header<String>(name = "User-Agent")
    val optionalHeader: String? by api.header<String?>(name = "Optional-Header")
}

@Endpoint
class BodyParamApi(api: EndpointAPI) {
    val value: TestPayload by api.body<TestPayload>()
}

@Endpoint
class CombinedParamsApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
    val userAgent: String by api.header<String>(name = "User-Agent")
    val payload: TestPayload by api.body<TestPayload>()
}

@Serializable
data class TestPayload(val value: String)

@Serializable
data class BodyRequest(val value: TestPayload) {
    fun request(): Request<BodyRequest, BodyParamApi> = Request(this, ::BodyParamApi, bodyRequestProperties)
}

private val bodyRequestProperties = properties(
    BodyRequest::value
)

@Serializable
data class PathParams(
    val id: Int,
    val optionalId: Int?
) {
    fun request(): Request<PathParams, PathParamApi> = Request(this, ::PathParamApi, pathProperties)
}

private val pathProperties = properties(
    PathParams::id,
    PathParams::optionalId
)

@Serializable
data class QueryParams(
    val name: String,
    val optionalName: String?
) {
    fun request(): Request<QueryParams, QueryParamApi> = Request(this, ::QueryParamApi, queryProperties)
}

private val queryProperties = properties(
    QueryParams::name,
    QueryParams::optionalName
)

@Serializable
data class HeaderParams(
    val userAgent: String,
    val optionalHeader: String?
) {
    fun request(): Request<HeaderParams, HeaderParamApi> = Request(this, ::HeaderParamApi, headerProperties)
}

private val headerProperties = properties(
    HeaderParams::userAgent,
    HeaderParams::optionalHeader
)

@Serializable
data class CombinedParams(
    val id: Int,
    val name: String,
    val userAgent: String,
    val payload: TestPayload
) {
    fun request(): Request<CombinedParams, CombinedParamsApi> = Request(this, ::CombinedParamsApi, combinedProperties)
}

private val combinedProperties = properties(
    CombinedParams::id,
    CombinedParams::name,
    CombinedParams::userAgent,
    CombinedParams::payload
)

fun <A> properties(vararg properties: KProperty1<A, *>): Map<String, KProperty1<A, *>> =
    properties.associateBy { it.name }

@Serializable
data class TestResponse(
    val id: Int? = null,
    val optionalId: Int? = null,
    val name: String? = null,
    val optionalName: String? = null,
    val userAgent: String? = null,
    val optionalHeader: String? = null,
    val payload: TestPayload? = null
)

class InputTest {
    
    @Test
    fun testPathParameters() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            
            route("/path/{id}/{optionalId}", HttpMethod.Get, ::PathParamApi) { api ->
                call.respond(TestResponse(id = api.id, optionalId = api.optionalId))
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        val response = client.get("/path/{id}/{optionalId}", PathParams(123, 456).request())
            .body<TestResponse>()
        assertEquals(123, response.id)
        assertEquals(456, response.optionalId)
    }
    
    @Test
    fun testQueryParameters() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            
            route("/query", HttpMethod.Get, ::QueryParamApi) { api ->
                call.respond(TestResponse(name = api.name, optionalName = api.optionalName))
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        val response = client.get("/query", QueryParams("test", "optional").request())
            .body<TestResponse>()
        assertEquals("test", response.name)
        assertEquals("optional", response.optionalName)
        
        val responseWithoutOptional = client.get("/query", QueryParams("test", null).request())
            .body<TestResponse>()
        assertEquals("test", responseWithoutOptional.name)
        assertNull(responseWithoutOptional.optionalName)
    }
    
    @Test
    fun testHeaderParameters() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            
            route("/header", HttpMethod.Get, ::HeaderParamApi) { api ->
                call.respond(TestResponse(userAgent = api.userAgent, optionalHeader = api.optionalHeader))
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        val response = client.get("/header", HeaderParams("TestAgent", "OptionalValue").request())
            .body<TestResponse>()
        assertEquals("TestAgent", response.userAgent)
        assertEquals("OptionalValue", response.optionalHeader)
    }
    
    @Test
    fun testBodyParameters() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            
            route("/body", HttpMethod.Post, ::BodyParamApi) { api ->
                call.respond(TestResponse(payload = api.value))
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        val testPayload = TestPayload("test-value")
        val bodyRequest = BodyRequest(testPayload)
        val response = client.post("/body", bodyRequest.request())
            .body<TestResponse>()
        
        assertEquals(testPayload, response.payload)
    }
    
    @Test
    fun testCombinedParameters() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            
            route("/combined/{id}", HttpMethod.Post, ::CombinedParamsApi) { api ->
                call.respond(TestResponse(
                    id = api.id,
                    name = api.name,
                    userAgent = api.userAgent,
                    payload = api.payload
                ))
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        val testPayload = TestPayload("test-value")
        val combinedParams = CombinedParams(123, "test", "TestAgent", testPayload)
        val response = client.post("/combined/{id}", combinedParams.request())
            .body<TestResponse>()
        
        assertEquals(123, response.id)
        assertEquals("test", response.name)
        assertEquals("TestAgent", response.userAgent)
        assertEquals(testPayload, response.payload)
    }
}