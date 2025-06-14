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
data class TestData(val value: String)

@Serializable
data class ValidationResponse(
    val username: String,
    val code: String?,
    val data: TestData
)

class ValidationTest {

    @Test
    fun `test numeric validation constraints`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/test/{value}",
                    p1 = Path.required<Int>(ge = 10, le = 100),
                    p2 = Query<Int?>(default = null, gt = 0, lt = 50, name = "limit")
                ) { value: Int, limit: Int? ->
                    call.respond(mapOf("value" to value.toString(), "limit" to limit?.toString()))
                }
            }
        }

        // Test valid values
        val validResponse = client.get("/test/50") {
            parameter("limit", "25")
        }
        assertEquals(HttpStatusCode.OK, validResponse.status)

        // Test path parameter too low
        val lowPathResponse = client.get("/test/5")
        assertEquals(HttpStatusCode.BadRequest, lowPathResponse.status)
        val lowPathText = lowPathResponse.bodyAsText()
        assert(lowPathText.contains("greater than or equal to 10"))

        // Test path parameter too high
        val highPathResponse = client.get("/test/150")
        assertEquals(HttpStatusCode.BadRequest, highPathResponse.status)
        val highPathText = highPathResponse.bodyAsText()
        assert(highPathText.contains("less than or equal to 100"))

        // Test query parameter at boundary (should fail)
        val boundaryQueryResponse = client.get("/test/50") {
            parameter("limit", "50")
        }
        assertEquals(HttpStatusCode.BadRequest, boundaryQueryResponse.status)
        val boundaryQueryText = boundaryQueryResponse.bodyAsText()
        assert(boundaryQueryText.contains("less than 50"))
    }

    @Test
    fun `test string validation constraints`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                post("/validate",
                    p1 = Query.required<String>(minLength = 3, maxLength = 10, name = "username"),
                    p2 = Query<String?>(default = null, regex = "[a-zA-Z0-9]+", name = "code"),
                    p3 = Body<TestData>()
                ) { username: String, code: String?, data: TestData ->
                    call.respond(ValidationResponse(
                        username = username,
                        code = code,
                        data = data
                    ))
                }
            }
        }

        // Test valid input
        val validResponse = client.post("/validate") {
            parameter("username", "alice")
            parameter("code", "ABC123")
            contentType(ContentType.Application.Json)
            setBody("""{"value": "test"}""")
        }
        assertEquals(HttpStatusCode.OK, validResponse.status)

        // Test username too short
        val shortUsernameResponse = client.post("/validate") {
            parameter("username", "ab")
            contentType(ContentType.Application.Json)
            setBody("""{"value": "test"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, shortUsernameResponse.status)
        val shortUsernameText = shortUsernameResponse.bodyAsText()
        assert(shortUsernameText.contains("at least 3 characters"))

        // Test username too long
        val longUsernameResponse = client.post("/validate") {
            parameter("username", "verylongusername")
            contentType(ContentType.Application.Json)
            setBody("""{"value": "test"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, longUsernameResponse.status)
        val longUsernameText = longUsernameResponse.bodyAsText()
        assert(longUsernameText.contains("at most 10 characters"))

        // Test invalid regex pattern
        val invalidRegexResponse = client.post("/validate") {
            parameter("username", "alice")
            parameter("code", "ABC-123")  // Contains hyphen which is not allowed
            contentType(ContentType.Application.Json)
            setBody("""{"value": "test"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, invalidRegexResponse.status)
        val invalidRegexText = invalidRegexResponse.bodyAsText()
        assert(invalidRegexText.contains("does not match the required pattern"))
    }

    @Test
    fun `test header parameter validation`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/headers",
                    p1 = Header.required<String>(regex = "Bearer .+", name = "authorization"),
                    p2 = Header<String?>(default = null, minLength = 5, name = "custom_header")
                ) { auth: String, custom: String? ->
                    call.respond(mapOf("auth" to auth, "custom" to custom))
                }
            }
        }

        // Test valid headers
        val validResponse = client.get("/headers") {
            header("authorization", "Bearer token123")
            header("custom-header", "value123")
        }
        assertEquals(HttpStatusCode.OK, validResponse.status)

        // Test invalid authorization header
        val invalidAuthResponse = client.get("/headers") {
            header("authorization", "Basic token123")  // Should be Bearer
        }
        assertEquals(HttpStatusCode.BadRequest, invalidAuthResponse.status)
        val invalidAuthText = invalidAuthResponse.bodyAsText()
        assert(invalidAuthText.contains("does not match the required pattern"))

        // Test custom header too short
        val shortHeaderResponse = client.get("/headers") {
            header("authorization", "Bearer token123")
            header("custom-header", "abc")  // Too short
        }
        assertEquals(HttpStatusCode.BadRequest, shortHeaderResponse.status)
        val shortHeaderText = shortHeaderResponse.bodyAsText()
        assert(shortHeaderText.contains("at least 5 characters"))
    }

    @Test
    fun `test nullable parameters with defaults`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/nullable",
                    p1 = Query<String?>(default = "default_value", name = "optional"),
                    p2 = Header<Int?>(default = 42, name = "count")
                ) { optional: String?, count: Int? ->
                    call.respond(mapOf("optional" to (optional ?: "null"), "count" to count.toString()))
                }
            }
        }

        // Test with no parameters (should use defaults)
        val defaultResponse = client.get("/nullable")
        if (defaultResponse.status != HttpStatusCode.OK) {
            val errorBody = defaultResponse.bodyAsText()
            println("Default Error: ${defaultResponse.status} - $errorBody")
        }
        assertEquals(HttpStatusCode.OK, defaultResponse.status)
        val defaultText = defaultResponse.bodyAsText()
        assert(defaultText.contains("\"optional\":\"default_value\""))
        assert(defaultText.contains("\"count\":\"42\""))

        // Test with provided parameters
        val providedResponse = client.get("/nullable") {
            parameter("optional", "custom")
            header("count", "100")
        }
        assertEquals(HttpStatusCode.OK, providedResponse.status)
        val providedText = providedResponse.bodyAsText()
        assert(providedText.contains("\"optional\":\"custom\""))
        assert(providedText.contains("\"count\":\"100\""))
    }

    @Test
    fun `test body parameter parsing errors`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                post("/body-test",
                    p1 = Body<TestData>()
                ) { data: TestData ->
                    call.respond(data)
                }
            }
        }

        // Test invalid JSON
        val invalidJsonResponse = client.post("/body-test") {
            contentType(ContentType.Application.Json)
            setBody("""{"invalid": json}""")  // Missing quotes around json
        }
        assertEquals(HttpStatusCode.BadRequest, invalidJsonResponse.status)

        // Test missing required field
        val missingFieldResponse = client.post("/body-test") {
            contentType(ContentType.Application.Json)
            setBody("""{}""")  // Missing "value" field
        }
        assertEquals(HttpStatusCode.BadRequest, missingFieldResponse.status)
    }

    @Test
    fun `test multiple path parameters`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/users/{userId}/posts/{postId}",
                    p1 = Path.required<Int>(ge = 1),
                    p2 = Path.required<String>(minLength = 3)
                ) { userId: Int, postId: String ->
                    call.respond(mapOf("userId" to userId.toString(), "postId" to postId))
                }
            }
        }

        // Test valid path parameters
        val validResponse = client.get("/users/123/posts/abc123")
        assertEquals(HttpStatusCode.OK, validResponse.status)
        val validText = validResponse.bodyAsText()
        assert(validText.contains("\"userId\":\"123\""))
        assert(validText.contains("\"postId\":\"abc123\""))

        // Test invalid userId (too low)
        val invalidUserResponse = client.get("/users/0/posts/abc123")
        assertEquals(HttpStatusCode.BadRequest, invalidUserResponse.status)

        // Test invalid postId (too short)
        val invalidPostResponse = client.get("/users/123/posts/ab")
        assertEquals(HttpStatusCode.BadRequest, invalidPostResponse.status)
    }
}