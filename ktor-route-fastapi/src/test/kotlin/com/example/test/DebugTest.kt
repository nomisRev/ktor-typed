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
data class DebugItem(val name: String, val price: Double)

class DebugTest {

    @Test
    fun `debug simple path parameter validation`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            


            routing {
                post("/items/{item_id}",
                    p1 = Path.required<Int>(ge = 1),
                    p2 = Body<DebugItem>()
                ) { itemId: Int, item: DebugItem ->
                    call.respond(mapOf("item_id" to itemId.toString(), "item" to item))
                }
            }
        }

        // Test with invalid item_id (should fail validation)
        try {
            val response = client.post("/items/0") {
                contentType(ContentType.Application.Json)
                setBody("""{"name": "Test Item", "price": 29.99}""")
            }

            println("Response status: ${response.status}")
            val responseText = response.bodyAsText()
            println("Response body: '$responseText'")
            println("Test timestamp: ${System.currentTimeMillis()}")
            
            // Let's see what we actually get
            if (response.status == HttpStatusCode.BadRequest) {
                println("Error message: '$responseText'")
                if (responseText.isEmpty()) {
                    println("ERROR: Response body is empty!")
                    assert(false) { "BadRequest response has empty body" }
                } else {
                    assert(responseText.contains("greater than or equal to 1")) { 
                        "Expected error message to contain 'greater than or equal to 1' but got: '$responseText'" 
                    }
                }
            } else {
                // If it's not BadRequest, let's see what happened
                println("Expected BadRequest but got ${response.status}")
                assert(false) { "Expected BadRequest but got ${response.status}" }
            }
        } catch (e: Exception) {
            println("Exception caught: ${e::class.simpleName}: ${e.message}")
            throw e
        }
    }

    @Test
    fun `debug parameter name resolution`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            


            routing {
                get("/debug/{test_param}",
                    p1 = Path.required<String>(),
                    p2 = Query<String?>(default = null, name = "query_param")
                ) { testParam: String, queryParam: String? ->
                    call.respond(mapOf(
                        "path_param" to testParam,
                        "query_param" to queryParam
                    ))
                }
            }
        }

        val response = client.get("/debug/hello") {
            parameter("query_param", "world")
        }

        println("Debug response status: ${response.status}")
        println("Debug response body: ${response.bodyAsText()}")
        
        assertEquals(HttpStatusCode.OK, response.status)
    }
}