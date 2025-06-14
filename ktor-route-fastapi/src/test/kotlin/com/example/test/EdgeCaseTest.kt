package com.example.test

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.route.fastapi.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class ComplexData(
    val id: Int,
    val name: String,
    val tags: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
data class ComplexDataResponse(
    val received: ComplexData,
    val tagCount: Int,
    val metadataKeys: List<String>
)

@Serializable
data class BooleanResponse(
    val flag: Boolean,
    val enabled: Boolean?
)

@Serializable
data class NullableBodyResponse(
    val message: String,
    val data: ComplexData? = null
)

class EdgeCaseTest {

    @Test
    fun `test empty and special string values`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/special/{value}",
                    p1 = Path.required<String>(),
                    p2 = Query<String?>(default = null, name = "special")
                ) { value: String, special: String? ->
                    call.respond(mapOf("value" to value, "special" to special))
                }
            }
        }

        // Test with URL-encoded special characters
        val specialResponse = client.get("/special/hello%20world") {
            parameter("special", "test@example.com")
        }
        assertEquals(HttpStatusCode.OK, specialResponse.status)
        val specialText = specialResponse.bodyAsText()
        assert(specialText.contains("hello world"))
        assert(specialText.contains("test@example.com"))
    }

    @Test
    fun `test numeric edge cases`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/numbers/{intVal}",
                    p1 = Path.required<Int>(),
                    p2 = Query<Long?>(default = null, name = "longVal"),
                    p3 = Query<Double?>(default = null, name = "doubleVal")
                ) { intVal: Int, longVal: Long?, doubleVal: Double? ->
                    call.respond(mapOf(
                            "intVal" to intVal.toString(),
                            "longVal" to longVal?.toString(),
                            "doubleVal" to doubleVal?.toString()
                        ))
                }
            }
        }

        // Test with maximum integer value
        val maxIntResponse = client.get("/numbers/${Int.MAX_VALUE}") {
            parameter("longVal", Long.MAX_VALUE.toString())
            parameter("doubleVal", "3.14159")
        }
        assertEquals(HttpStatusCode.OK, maxIntResponse.status)

        // Test with negative numbers
        val negativeResponse = client.get("/numbers/-42") {
            parameter("longVal", "-1000")
            parameter("doubleVal", "-2.5")
        }
        assertEquals(HttpStatusCode.OK, negativeResponse.status)

        // Test with invalid number format
        val invalidResponse = client.get("/numbers/not-a-number")
        assertEquals(HttpStatusCode.BadRequest, invalidResponse.status)
    }

    @Test
    fun `test complex body structures`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                post("/complex",
                    p1 = Body<ComplexData>()
                ) { data: ComplexData ->
                    call.respond(ComplexDataResponse(
                        received = data,
                        tagCount = data.tags.size,
                        metadataKeys = data.metadata.keys.toList()
                    ))
                }
            }
        }

        // Test with complex nested structure
        val complexResponse = client.post("/complex") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "id": 123,
                    "name": "Test Item",
                    "tags": ["tag1", "tag2", "tag3"],
                    "metadata": {
                        "category": "test",
                        "priority": "high",
                        "version": "1.0"
                    }
                }
            """.trimIndent())
        }
        assertEquals(HttpStatusCode.OK, complexResponse.status)
        val complexText = complexResponse.bodyAsText()
        assert(complexText.contains("\"tagCount\":3"))
        assert(complexText.contains("category"))
        assert(complexText.contains("priority"))
        assert(complexText.contains("version"))
    }

    @Test
    fun `test header case sensitivity and underscore conversion`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/headers-test",
                    p1 = Header<String?>(default = null, name = "user_agent"),
                    p2 = Header<String?>(default = null, name = "x_custom_header"),
                    p3 = Header<String?>(default = null, name = "authorization")
                ) { userAgent: String?, customHeader: String?, auth: String? ->
                    call.respond(mapOf(
                            "userAgent" to (userAgent ?: "null"),
                            "customHeader" to (customHeader ?: "null"),
                            "auth" to (auth ?: "null")
                        ))
                }
            }
        }

        // Test with various header formats
        val headerResponse = client.get("/headers-test") {
            header("User-Agent", "TestAgent/1.0")
            header("X-Custom-Header", "custom-value")
            header("Authorization", "Bearer token123")
        }
        assertEquals(HttpStatusCode.OK, headerResponse.status)
        val headerText = headerResponse.bodyAsText()
        assert(headerText.contains("TestAgent/1.0"))
        assert(headerText.contains("custom-value"))
        assert(headerText.contains("Bearer token123"))
    }

    @Test
    fun `test boolean parameter handling`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/boolean/{flag}",
                    p1 = Path.required<Boolean>(),
                    p2 = Query<Boolean?>(default = null, name = "enabled")
                ) { flag: Boolean, enabled: Boolean? ->
                    call.respond(BooleanResponse(flag = flag, enabled = enabled))
                }
            }
        }

        // Test with true values
        val trueResponse = client.get("/boolean/true") {
            parameter("enabled", "true")
        }
        assertEquals(HttpStatusCode.OK, trueResponse.status)
        val trueText = trueResponse.bodyAsText()
        assert(trueText.contains("\"flag\":true"))
        assert(trueText.contains("\"enabled\":true"))

        // Test with false values
        val falseResponse = client.get("/boolean/false") {
            parameter("enabled", "false")
        }
        assertEquals(HttpStatusCode.OK, falseResponse.status)
        val falseText = falseResponse.bodyAsText()
        assert(falseText.contains("\"flag\":false"))
        assert(falseText.contains("\"enabled\":false"))

        // Test with invalid boolean
        val invalidBoolResponse = client.get("/boolean/maybe")
        assertEquals(HttpStatusCode.BadRequest, invalidBoolResponse.status)
    }

    @Test
    fun `test parameter type conversion errors`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/convert/{id}",
                    p1 = Path.required<Int>(),
                    p2 = Query<Double?>(default = null, name = "rate"),
                    p3 = Query<Long?>(default = null, name = "timestamp")
                ) { id: Int, rate: Double?, timestamp: Long? ->
                    call.respond(mapOf("id" to id.toString(), "rate" to rate?.toString(), "timestamp" to timestamp?.toString()))
                }
            }
        }

        // Test invalid integer conversion
        val invalidIntResponse = client.get("/convert/abc")
        assertEquals(HttpStatusCode.BadRequest, invalidIntResponse.status)
        val invalidIntText = invalidIntResponse.bodyAsText()
        assert(invalidIntText.contains("Invalid value"))

        // Test invalid double conversion
        val invalidDoubleResponse = client.get("/convert/123") {
            parameter("rate", "not-a-number")
        }
        assertEquals(HttpStatusCode.BadRequest, invalidDoubleResponse.status)

        // Test invalid long conversion
        val invalidLongResponse = client.get("/convert/123") {
            parameter("timestamp", "invalid-timestamp")
        }
        assertEquals(HttpStatusCode.BadRequest, invalidLongResponse.status)
    }

    @Test
    fun `test nullable body parameters`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                post("/nullable-body",
                    p1 = Body<ComplexData?>()
                ) { data: ComplexData? ->
                    if (data == null) {
                        call.respond(NullableBodyResponse(message = "No data provided"))
                    } else {
                        call.respond(NullableBodyResponse(message = "Data received", data = data))
                    }
                }
            }
        }

        // Test with valid body
        val validBodyResponse = client.post("/nullable-body") {
            contentType(ContentType.Application.Json)
            setBody("""{"id": 1, "name": "test"}""")
        }
        assertEquals(HttpStatusCode.OK, validBodyResponse.status)

        // Test with empty body (should handle gracefully)
        val emptyBodyResponse = client.post("/nullable-body") {
            contentType(ContentType.Application.Json)
            setBody("")
        }
        // This might be OK or BadRequest depending on implementation
        assert(emptyBodyResponse.status == HttpStatusCode.OK || emptyBodyResponse.status == HttpStatusCode.BadRequest)
    }

    @Test
    fun `test regex validation edge cases`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/regex-test",
                    p1 = Query.required<String>(regex = "^[A-Z]{2,3}[0-9]{3,4}$", name = "code"),
                    p2 = Query<String?>(default = null, regex = "[a-z]+@[a-z]+\\.[a-z]+", name = "email")
                ) { code: String, email: String? ->
                    call.respond(mapOf("code" to code, "email" to email))
                }
            }
        }

        // Test valid patterns
        val validResponse = client.get("/regex-test") {
            parameter("code", "ABC123")
            parameter("email", "test@example.com")
        }
        assertEquals(HttpStatusCode.OK, validResponse.status)

        // Test invalid code pattern
        val invalidCodeResponse = client.get("/regex-test") {
            parameter("code", "abc123")  // Should be uppercase
        }
        assertEquals(HttpStatusCode.BadRequest, invalidCodeResponse.status)

        // Test invalid email pattern
        val invalidEmailResponse = client.get("/regex-test") {
            parameter("code", "XYZ456")
            parameter("email", "invalid-email")
        }
        assertEquals(HttpStatusCode.BadRequest, invalidEmailResponse.status)
    }
}