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
data class User(val name: String, val age: Int)

class TypeSafetyTest {

    @Test
    fun `test type-safe routing with Input interface`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                // Test that p1 is typed as Path<Int> and provides Int in lambda
                get("/users/{userId}",
                    p1 = Path.required<Int>(ge = 1),
                    p2 = Query<String?>(default = "Unknown")
                ) { userId: Int, name: String? ->
                    // userId should be Int (type-safe)
                    // name should be String? (type-safe)
                    call.respond(mapOf(
                        "userId" to userId,
                        "name" to name,
                        "userIdType" to userId::class.simpleName
                    ))
                }

                // Test Body parameter type safety
                post("/users",
                    p1 = Body<User>()
                ) { user: User ->
                    // user should be User (type-safe)
                    call.respond(mapOf(
                        "received" to user,
                        "userType" to user::class.simpleName
                    ))
                }
            }
        }

        // Test GET route
        val getResponse = client.get("/users/123") {
            parameter("name", "John")
        }
        assertEquals(HttpStatusCode.OK, getResponse.status)
        val getResponseText = getResponse.bodyAsText()
        assert(getResponseText.contains("\"userId\":123"))
        assert(getResponseText.contains("\"name\":\"John\""))
        assert(getResponseText.contains("\"userIdType\":\"Int\""))

        // Test POST route
        val postResponse = client.post("/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name": "Alice", "age": 30}""")
        }
        assertEquals(HttpStatusCode.OK, postResponse.status)
        val postResponseText = postResponse.bodyAsText()
        assert(postResponseText.contains("\"name\":\"Alice\""))
        assert(postResponseText.contains("\"age\":30"))
        assert(postResponseText.contains("\"userType\":\"User\""))
    }
}