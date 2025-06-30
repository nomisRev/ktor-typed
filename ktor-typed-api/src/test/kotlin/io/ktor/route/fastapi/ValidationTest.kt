package io.ktor.route.fastapi

import io.ktor.http.HttpMethod
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

@Endpoint
class StringValidationApi(api: EndpointAPI) {
    val notEmptyString by api.query<String>(validation = Validation.string().notEmpty())
    val minLengthString by api.query<String>(validation = Validation.string().minLength(3))
    val maxLengthString by api.query<String>(validation = Validation.string().maxLength(5))
    val regexString by api.query<String>(validation = Validation.string().regex("^[a-z]+$"))
}

@Endpoint
class NumberValidationApi(api: EndpointAPI) {
    val minValue by api.query<Int>(validation = Validation.int().min(5))
    val maxValue by api.query<Int>(validation = Validation.int().max(10))
    val rangeValue by api.query<Int>(validation = Validation.int().range(5, 10))
    val positiveValue by api.query<Int>(validation = Validation.int().positive())
}

@Serializable
data class StringValidationRequest(
    val notEmptyString: String,
    val minLengthString: String,
    val maxLengthString: String,
    val regexString: String
) {
    fun request(): Request<StringValidationRequest, StringValidationApi> = 
        Request(this, ::StringValidationApi)
}

@Serializable
data class NumberValidationRequest(
    val minValue: Int,
    val maxValue: Int,
    val rangeValue: Int,
    val positiveValue: Int
) {
    fun request(): Request<NumberValidationRequest, NumberValidationApi> = 
        Request(this, ::NumberValidationApi)
}

@Serializable
data class ValidationResponse(val success: Boolean = true, val message: String = "")

class ValidationTest {
    
    @Test
    fun testValidStringParameters() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            
            route("/string-validation", HttpMethod.Get, ::StringValidationApi) { api ->
                call.respond(ValidationResponse(true))
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        val validParams = StringValidationRequest(
            notEmptyString = "not-empty",
            minLengthString = "abc",
            maxLengthString = "abcde",
            regexString = "abcdef"
        )
        
        val response = client.get("/string-validation", validParams.request())
            .body<ValidationResponse>()
        
        assertTrue(response.success)
    }
    
    @Test
    fun testInvalidStringParameters() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            
            route("/string-validation", HttpMethod.Get, ::StringValidationApi) { api ->
                try {
                    // This should throw an exception due to validation failure
                    val notEmpty = api.notEmptyString
                    val minLength = api.minLengthString
                    val maxLength = api.maxLengthString
                    val regex = api.regexString
                    call.respond(ValidationResponse(true))
                } catch (e: Exception) {
                    call.respond(ValidationResponse(false, e.message ?: "Validation failed"))
                }
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        // Test empty string
        val invalidEmptyParams = StringValidationRequest(
            notEmptyString = "",
            minLengthString = "abc",
            maxLengthString = "abcde",
            regexString = "abcdef"
        )
        
        val emptyResponse = client.get("/string-validation", invalidEmptyParams.request())
            .body<ValidationResponse>()
        
        assertFalse(emptyResponse.success)
        
        // Test too short string
        val invalidShortParams = StringValidationRequest(
            notEmptyString = "not-empty",
            minLengthString = "ab", // Too short
            maxLengthString = "abcde",
            regexString = "abcdef"
        )
        
        val shortResponse = client.get("/string-validation", invalidShortParams.request())
            .body<ValidationResponse>()
        
        assertFalse(shortResponse.success)
        
        // Test too long string
        val invalidLongParams = StringValidationRequest(
            notEmptyString = "not-empty",
            minLengthString = "abc",
            maxLengthString = "abcdef", // Too long
            regexString = "abcdef"
        )
        
        val longResponse = client.get("/string-validation", invalidLongParams.request())
            .body<ValidationResponse>()
        
        assertFalse(longResponse.success)
        
        // Test invalid regex
        val invalidRegexParams = StringValidationRequest(
            notEmptyString = "not-empty",
            minLengthString = "abc",
            maxLengthString = "abcde",
            regexString = "ABC123" // Contains uppercase and numbers
        )
        
        val regexResponse = client.get("/string-validation", invalidRegexParams.request())
            .body<ValidationResponse>()
        
        assertFalse(regexResponse.success)
    }
    
    @Test
    fun testValidNumberParameters() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            
            route("/number-validation", HttpMethod.Get, ::NumberValidationApi) { api ->
                call.respond(ValidationResponse(true))
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        val validParams = NumberValidationRequest(
            minValue = 10,
            maxValue = 5,
            rangeValue = 7,
            positiveValue = 5
        )
        
        val response = client.get("/number-validation", validParams.request())
            .body<ValidationResponse>()
        
        assertTrue(response.success)
    }
    
    @Test
    fun testInvalidNumberParameters() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            
            route("/number-validation", HttpMethod.Get, ::NumberValidationApi) { api ->
                try {
                    // This should throw an exception due to validation failure
                    val min = api.minValue
                    val max = api.maxValue
                    val range = api.rangeValue
                    val positive = api.positiveValue
                    call.respond(ValidationResponse(true))
                } catch (e: Exception) {
                    call.respond(ValidationResponse(false, e.message ?: "Validation failed"))
                }
            }
        }
        
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }
        
        // Test too small value
        val invalidMinParams = NumberValidationRequest(
            minValue = 3, // Less than min(5)
            maxValue = 5,
            rangeValue = 7,
            positiveValue = 5
        )
        
        val minResponse = client.get("/number-validation", invalidMinParams.request())
            .body<ValidationResponse>()
        
        assertFalse(minResponse.success)
        
        // Test too large value
        val invalidMaxParams = NumberValidationRequest(
            minValue = 10,
            maxValue = 15, // Greater than max(10)
            rangeValue = 7,
            positiveValue = 5
        )
        
        val maxResponse = client.get("/number-validation", invalidMaxParams.request())
            .body<ValidationResponse>()
        
        assertFalse(maxResponse.success)
        
        // Test out of range value
        val invalidRangeParams = NumberValidationRequest(
            minValue = 10,
            maxValue = 5,
            rangeValue = 15, // Out of range(5, 10)
            positiveValue = 5
        )
        
        val rangeResponse = client.get("/number-validation", invalidRangeParams.request())
            .body<ValidationResponse>()
        
        assertFalse(rangeResponse.success)
        
        // Test negative value
        val invalidPositiveParams = NumberValidationRequest(
            minValue = 10,
            maxValue = 5,
            rangeValue = 7,
            positiveValue = -1 // Not positive
        )
        
        val positiveResponse = client.get("/number-validation", invalidPositiveParams.request())
            .body<ValidationResponse>()
        
        assertFalse(positiveResponse.success)
    }
}