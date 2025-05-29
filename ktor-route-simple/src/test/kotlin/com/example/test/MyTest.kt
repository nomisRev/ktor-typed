package com.example.test

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.route.simple.route
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import com.example.test.CreateUser
import com.example.test.JsonBody
import com.example.test.TwoBodies

class MyTest {
    @Test
    fun test() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<CreateUser>("/users/{userId}/create") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val user = client.get("/users/123/create?name=John&age=30") {
                headers.append("X-flag", "true")
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Json)
                setBody(JsonBody("Hello World"))
            }.body<CreateUser>()

            assertEquals(
                CreateUser(
                    userId = "123",
                    NAME = "John",
                    age = 30,
                    many = listOf("a", "b", "c"),
                    header = true,
                    body = JsonBody("Hello World")
                ),
                user
            )
        }
    }

    @Test
    fun missingHeader() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<CreateUser>("/users/{userId}/create") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/users/123/create?name=John&age=30") {
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Json)
                setBody(JsonBody("Hello World"))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun missingQuery() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<CreateUser>("/users/{userId}/create") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/users/123/create?name=John&age=30") {
                headers.append("X-flag", "true")
                contentType(ContentType.Application.Json)
                setBody(JsonBody("Hello World"))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun missingBody() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<CreateUser>("/users/{userId}/create") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/users/123/create?name=John&age=30") {
                headers.append("X-flag", "true")
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Json)
            }
            // When Content-Type is set but no body is provided, the server returns 415 Unsupported Media Type
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
    }

    @Test
    fun missingPath() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<CreateUser>("/users/{userId}/create") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/users/123/create?name=John") {
                headers.append("X-flag", "true")
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Json)
                setBody(JsonBody("Hello World"))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun twoBodies() {
        val message = assertThrows<IllegalArgumentException> {
            testApplication {
                routing {
                    install(ServerContentNegotiation) { json() }
                    route<TwoBodies>("/two-bodies") { value ->
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }
        }.message

        assertEquals(
            "Only a single or no @Body annotation is allowed but found 'one', 'two' in TwoBodies",
            message
        )
    }
}
