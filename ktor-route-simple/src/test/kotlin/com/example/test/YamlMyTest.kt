package com.example.test

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.route.simple.get
import io.ktor.route.simple.yaml
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class YamlMyTest {

    @Test
    fun test() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { yaml() }
                get<CreateUser>("/users/{userId}/create") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { yaml() }
            }

            val user = client.get("/users/123/create?name=John&age=30") {
                headers.append("X-flag", "true")
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Yaml)
                setBody(Body("Hello World"))
            }.body<CreateUser>()

            assertEquals(
                CreateUser(
                    userId = "123",
                    NAME = "John",
                    age = 30,
                    many = listOf("a", "b", "c"),
                    header = true,
                    body = Body("Hello World")
                ),
                user
            )
        }
    }

    @Test
    fun missingHeader() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { yaml() }
                get<CreateUser>("/users/{userId}/create") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { yaml() }
            }

            val response = client.get("/users/123/create?name=John&age=30") {
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Yaml)
                setBody(Body("Hello World"))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun missingQuery() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { yaml() }
                get<CreateUser>("/users/{userId}/create") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { yaml() }
            }

            val response = client.get("/users/123/create?name=John") {
                headers.append("X-flag", "true")
                contentType(ContentType.Application.Yaml)
                setBody(Body("Hello World"))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun missingBody() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { yaml() }
                get<CreateUser>("/users/{userId}/create") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { yaml() }
            }

            val response = client.get("/users/123/create?name=John&age=30") {
                headers.append("X-flag", "true")
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Json)
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun missingPath() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { yaml() }
                get<CreateUser>("/users/{userId}/create") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { yaml() }
            }

            val response = client.get("/users/123/create?name=John") {
                headers.append("X-flag", "true")
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Yaml)
                setBody(Body("Hello World"))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun twoBodies() {
        val message = assertThrows<IllegalArgumentException> {
            testApplication {
                routing {
                    install(ServerContentNegotiation) { yaml() }
                    get<TwoBodies>("/two-bodies") { value ->
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
