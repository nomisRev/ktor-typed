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
data class Item(val name: String, val price: Double)

@Serializable
data class UpdateItemResponse(
    val item_id: Int,
    val item: Item,
    val q: String? = null,
    val user_agent: String? = null,
    val x_token: String? = null
)

class FastAPIExampleTest {

    @Test
    fun `test FastAPI-style PUT route with all parameter types`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                put("/items/{item_id}",
                    p1 = Path.required<Int>(title = "The ID of the item to get", ge = 1),
                    p2 = Query<String?>(default = null, minLength = 3, maxLength = 50, description = "A query string"),
                    p3 = Body<Item>(), // Item will be parsed from body automatically
                    p4 = Header<String?>(default = null, description = "The user agent of the client"),
                    p5 = Header<String?>(default = null, description = "A custom header token")
                ) { itemId: Int, q: String?, item: Item, userAgent: String?, xToken: String? ->

                    val results = UpdateItemResponse(
                        item_id = itemId,
                        item = item,
                        q = q,
                        user_agent = userAgent,
                        x_token = xToken
                    )
                    call.respond(results)
                }
            }
        }

        val response = client.put("/items/123") {
            parameter("q", "test query")
            header("user-agent", "test-agent")
            header("x-token", "secret-token")
            contentType(ContentType.Application.Json)
            setBody("""{"name": "Test Item", "price": 29.99}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val responseText = response.bodyAsText()

        // Verify the response contains expected values
        assert(responseText.contains("\"item_id\":123"))
        assert(responseText.contains("\"name\":\"Test Item\""))
        assert(responseText.contains("\"price\":29.99"))
        assert(responseText.contains("\"q\":\"test query\""))
        assert(responseText.contains("\"user_agent\":\"test-agent\""))
        assert(responseText.contains("\"x_token\":\"secret-token\""))
    }

    @Test
    fun `test validation with Path parameter constraints`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                put("/items/{item_id}",
                    p1 = io.ktor.route.fastapi.Path.required<Int>(ge = 1), // item_id must be >= 1
                    p2 = io.ktor.route.fastapi.Query<String?>(default = null),
                    p3 = io.ktor.route.fastapi.Body<Item>(),
                    p4 = io.ktor.route.fastapi.Header<String?>(default = null),
                    p5 = io.ktor.route.fastapi.Header<String?>(default = null)
                ) { itemId: Int, q: String?, item: Item, userAgent: String?, xToken: String? ->
                    call.respond(mapOf("item_id" to itemId))
                }
            }
        }

        // Test with invalid item_id (should fail validation)
        val response = client.put("/items/0") {
            contentType(ContentType.Application.Json)
            setBody("""{"name": "Test Item", "price": 29.99}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val responseText = response.bodyAsText()
        assert(responseText.contains("greater than or equal to 1"))
    }

    @Test
    fun `test Query parameter with length constraints`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                put("/items/{item_id}",
                    p1 = io.ktor.route.fastapi.Path.required<Int>(),
                    p2 = io.ktor.route.fastapi.Query.required<String>(minLength = 3, maxLength = 10),
                    p3 = io.ktor.route.fastapi.Body<Item>(),
                    p4 = io.ktor.route.fastapi.Header<String?>(default = null),
                    p5 = io.ktor.route.fastapi.Header<String?>(default = null)
                ) { itemId: Int, q: String, item: Item, userAgent: String?, xToken: String? ->
                    call.respond(mapOf("q" to q))
                }
            }
        }

        // Test with query parameter that's too short
        val response = client.put("/items/123") {
            parameter("q", "ab") // Only 2 characters, minimum is 3
            contentType(ContentType.Application.Json)
            setBody("""{"name": "Test Item", "price": 29.99}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val responseText = response.bodyAsText()
        assert(responseText.contains("at least 3 characters"))
    }

    @Test
    fun `test missing required parameters`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                put("/items/{item_id}",
                    Path.required(),
                    Query.required(), // Required query parameter
                    Body(),
                    Header(default = null),
                    Header(default = null)
                ) { itemId: Int, q: String, item: Item, userAgent: String?, xToken: String? ->
                    call.respond(mapOf("q" to q))
                }
            }
        }

        // Test without required query parameter
        val response = client.put("/items/123") {
            contentType(ContentType.Application.Json)
            setBody("""{"name": "Test Item", "price": 29.99}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        val responseText = response.bodyAsText()
        assert(responseText.contains("Missing required query parameter"))
    }
}