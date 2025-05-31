package com.example.test

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.route.simple.route
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@kotlinx.serialization.Serializable
data class NullablePathModel(
    val requiredParam: String,
    val nullableParam: String?
)

class NullablePathTest {
    @Test
    fun testNullablePathWithValue() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<NullablePathModel>("/path/{requiredParam}/{nullableParam}") { value ->
                    assertEquals("required", value.requiredParam)
                    assertEquals("nullable", value.nullableParam)
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/path/required/nullable")
            assertEquals(HttpStatusCode.OK, response.status)
            
            val nullablePath = response.body<NullablePathModel>()
            assertEquals("required", nullablePath.requiredParam)
            assertEquals("nullable", nullablePath.nullableParam)
        }
    }

    @Test
    fun testNullablePathWithNullValue() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<NullablePathModel>("/path/{requiredParam}") { value ->
                    assertEquals("required", value.requiredParam)
                    assertNull(value.nullableParam)
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/path/required")
            assertEquals(HttpStatusCode.OK, response.status)
            
            val nullablePath = response.body<NullablePathModel>()
            assertEquals("required", nullablePath.requiredParam)
            assertNull(nullablePath.nullableParam)
        }
    }

    @Test
    fun testMissingRequiredPath() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<NullablePathModel>("/path/{requiredParam}") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/path")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }
}