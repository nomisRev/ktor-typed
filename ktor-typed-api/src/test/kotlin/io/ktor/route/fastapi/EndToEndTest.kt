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

@Serializable
data class EndToEndTestBody(val value: String)

@Endpoint
class EndToEndApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
    val header: String by api.header<String>()
    val body: EndToEndTestBody by api.body<EndToEndTestBody>()
}

@Serializable
data class EndToEndParams(
    val id: Int,
    val name: String,
    val header: String,
    val body: EndToEndTestBody
) {
    fun request(): Request<EndToEndParams, EndToEndApi> = 
        Request(this, ::EndToEndApi)
}

@Serializable
data class EndToEndResponse(
    val id: Int,
    val name: String,
    val header: String,
    val body: EndToEndTestBody,
    val message: String
)

class EndToEndTest {
    
    @Test
    fun testCompleteFlow() = testApplication {
        // Set up server
        routing {
            install(ContentNegotiation) { json() }
            
            route("/end-to-end/{id}", HttpMethod.Post, ::EndToEndApi) { api ->
                // Process the request
                val processedId = api.id * 2
                val processedName = api.name.uppercase()
                val processedHeader = "Processed-${api.header}"
                val processedBody = EndToEndTestBody("Processed-${api.body.value}")
                
                // Respond
                call.respond(EndToEndResponse(
                    id = processedId,
                    name = processedName,
                    header = processedHeader,
                    body = processedBody,
                    message = "Request processed successfully"
                ))
            }
        }
        
        // Set up client
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        // Create request parameters
        val testBody = EndToEndTestBody("test-value")
        val params = EndToEndParams(123, "test-name", "test-header", testBody)
        
        // Make request
        val response = client.post("/end-to-end/{id}", params.request())
            .body<EndToEndResponse>()
        
        // Verify response
        assertEquals(246, response.id) // 123 * 2
        assertEquals("TEST-NAME", response.name) // uppercase
        assertEquals("Processed-test-header", response.header)
        assertEquals("Processed-test-value", response.body.value)
        assertEquals("Request processed successfully", response.message)
    }
    
    @Test
    fun testErrorHandling() = testApplication {
        @Endpoint
        class ErrorApi(api: EndpointAPI) {
            val shouldError: Boolean by api.query<Boolean>()
        }
        
        @Serializable
        data class ErrorParams(val shouldError: Boolean) {
            fun request(): Request<ErrorParams, ErrorApi> = 
                Request(this, ::ErrorApi)
        }
        
        @Serializable
        data class ErrorResponse(val success: Boolean, val message: String)
        
        // Set up server
        routing {
            install(ContentNegotiation) { json() }
            
            route("/error-test", HttpMethod.Get, ::ErrorApi) { api ->
                if (api.shouldError) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse(false, "Error occurred"))
                } else {
                    call.respond(ErrorResponse(true, "No error"))
                }
            }
        }
        
        // Set up client
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        // Test success case
        val successParams = ErrorParams(false)
        val successResponse = client.get("/error-test", successParams.request())
            .body<ErrorResponse>()
        
        assertEquals(true, successResponse.success)
        assertEquals("No error", successResponse.message)
        
        // Test error case
        val errorParams = ErrorParams(true)
        val errorResponse = client.get("/error-test", errorParams.request())
        
        assertEquals(HttpStatusCode.BadRequest, errorResponse.status)
        
        val errorBody = errorResponse.body<ErrorResponse>()
        assertEquals(false, errorBody.success)
        assertEquals("Error occurred", errorBody.message)
    }
    
    @Test
    fun testComplexDataStructures() = testApplication {
        @Serializable
        data class NestedData(val nestedValue: String)
        
        @Serializable
        data class ComplexBody(
            val stringValue: String,
            val intValue: Int,
            val booleanValue: Boolean,
            val nestedData: NestedData,
            val listValues: List<String>
        )
        
        @Endpoint
        class ComplexApi(api: EndpointAPI) {
            val complexBody: ComplexBody by api.body<ComplexBody>()
        }
        
        @Serializable
        data class ComplexParams(val complexBody: ComplexBody) {
            fun request(): Request<ComplexParams, ComplexApi> = 
                Request(this, ::ComplexApi)
        }
        
        @Serializable
        data class ComplexResponse(val processed: Boolean, val body: ComplexBody)
        
        // Set up server
        routing {
            install(ContentNegotiation) { json() }
            
            route("/complex-test", HttpMethod.Post, ::ComplexApi) { api ->
                // Process the complex body
                val processedBody = api.complexBody.copy(
                    stringValue = api.complexBody.stringValue.uppercase(),
                    intValue = api.complexBody.intValue * 2,
                    nestedData = NestedData("Processed-${api.complexBody.nestedData.nestedValue}"),
                    listValues = api.complexBody.listValues.map { it.uppercase() }
                )
                
                call.respond(ComplexResponse(true, processedBody))
            }
        }
        
        // Set up client
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        // Create complex request
        val complexBody = ComplexBody(
            stringValue = "test-string",
            intValue = 42,
            booleanValue = true,
            nestedData = NestedData("nested-value"),
            listValues = listOf("item1", "item2", "item3")
        )
        
        val params = ComplexParams(complexBody)
        
        // Make request
        val response = client.post("/complex-test", params.request())
            .body<ComplexResponse>()
        
        // Verify response
        assertEquals(true, response.processed)
        assertEquals("TEST-STRING", response.body.stringValue)
        assertEquals(84, response.body.intValue) // 42 * 2
        assertEquals(true, response.body.booleanValue)
        assertEquals("Processed-nested-value", response.body.nestedData.nestedValue)
        assertEquals(listOf("ITEM1", "ITEM2", "ITEM3"), response.body.listValues)
    }
}