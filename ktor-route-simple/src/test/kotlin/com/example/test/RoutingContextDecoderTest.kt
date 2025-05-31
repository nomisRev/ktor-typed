package com.example.test

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.route.simple.RoutingContextDecoder
import io.ktor.route.simple.test
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RoutingContextDecoderTest {
    @Test
    fun testCreateUserDecoding() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                test("/users/{userId}/create") { create: CreateUser ->
                    call.respond(HttpStatusCode.OK, create)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/users/123/create?NAME=John&age=30") {
                headers.append("X-flag", "true")
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Json)
                setBody(JsonBody("Hello World"))
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(
                CreateUser("123", "John", 30, listOf("a", "b", "c"), true, JsonBody("Hello World")),
                response.body<CreateUser>()
            )
        }
    }

    @Test
    fun testMissingRequiredParameter() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                test("/users/{userId}/create") { create: CreateUser ->
                    call.respond(HttpStatusCode.OK, create)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            // Missing age parameter
            val response = client.get("/users/123/create?NAME=John") {
                headers.append("X-flag", "true")
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Json)
                setBody(JsonBody("Hello World"))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun testMissingRequiredHeader() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                test("/users/{userId}/create") { value: CreateUser ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            // Missing X-flag header
            val response = client.get("/users/123/create?NAME=John&age=30") {
                url.parameters.appendAll("many", listOf("a", "b", "c"))
                contentType(ContentType.Application.Json)
                setBody(JsonBody("Hello World"))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    // TODO: proper error handling when a body is missing
    @Test
    @Ignore
    fun testMissingRequiredBody() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                test("/users/{userId}/create") { value: CreateUser ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            // Missing body
            val response = client.get("/users/123/create?NAME=John&age=30") {
                headers.append("X-flag", "true")
                url.parameters.appendAll("many", listOf("a", "b", "c"))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun testListDecoding() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                test("/list-test") { value: ListTest ->
                    call.respond(HttpStatusCode.OK, mapOf("success" to true))
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/list-test") {
                url.parameters.appendAll("stringList", listOf("a", "b", "c"))
                url.parameters.appendAll("intList", listOf("1", "2", "3"))
            }

            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun testEmptyListDecoding() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                test("/list-test") { value: ListTest ->
                    assertEquals(listOf("a", "b", "c"), value.stringList)
                    assertEquals(emptyList(), value.intList)
                    call.respond(HttpStatusCode.OK, mapOf("success" to true))
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            // Only providing one list parameter
            val response = client.get("/list-test") {
                url.parameters.appendAll("stringList", listOf("a", "b", "c"))
            }

            assertEquals(HttpStatusCode.OK, response.status)
        }
    }

    @Test
    fun testAllPrimitiveTypes() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                test("/primitives") { value: PrimitiveTypes ->
                    assertEquals("test", value.stringValue)
                    assertEquals(42, value.intValue)
                    assertEquals(9223372036854775807L, value.longValue)
                    assertEquals(3.14159, value.doubleValue)
                    assertEquals(2.71828f, value.floatValue)
                    assertEquals(true, value.booleanValue)
                    assertEquals(127.toByte(), value.byteValue)
                    assertEquals('A', value.charValue)
                    assertEquals(32767.toShort(), value.shortValue)
                    call.respond(HttpStatusCode.OK, mapOf("success" to true))
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response =
                client.get("/primitives?stringValue=test&intValue=42&longValue=9223372036854775807&doubleValue=3.14159&floatValue=2.71828&booleanValue=true&byteValue=127&charValue=A&shortValue=32767")

            assertEquals(HttpStatusCode.OK, response.status)
        }
    }
}

@kotlinx.serialization.Serializable
data class ListTest(
    val stringList: List<String> = emptyList(),
    val intList: List<Int> = emptyList()
)

@kotlinx.serialization.Serializable
data class PrimitiveTypes(
    val stringValue: String,
    val intValue: Int,
    val longValue: Long,
    val doubleValue: Double,
    val floatValue: Float,
    val booleanValue: Boolean,
    val byteValue: Byte,
    val charValue: Char,
    val shortValue: Short
)
