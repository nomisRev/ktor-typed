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
data class NullableListModel(
    val requiredList: List<String>,
    val nullableList: List<String>?
)

class NullableListTest {
    @Test
    fun testNullableListWithValue() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<NullableListModel>("/nullable-list") { value ->
                    assertEquals(listOf("a", "b", "c"), value.requiredList)
                    assertEquals(listOf("x", "y", "z"), value.nullableList)
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/nullable-list") {
                url.parameters.appendAll("requiredList", listOf("a", "b", "c"))
                url.parameters.appendAll("nullableList", listOf("x", "y", "z"))
            }
            assertEquals(HttpStatusCode.OK, response.status)

            val nullableList = response.body<NullableListModel>()
            assertEquals(listOf("a", "b", "c"), nullableList.requiredList)
            assertEquals(listOf("x", "y", "z"), nullableList.nullableList)
        }
    }

    @Test
    fun testNullableListWithNullValue() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<NullableListModel>("/nullable-list") { value ->
                    assertEquals(listOf("a", "b", "c"), value.requiredList)
                    assertNull(value.nullableList)
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/nullable-list") {
                url.parameters.appendAll("requiredList", listOf("a", "b", "c"))
                // Not providing nullableList to test null handling
            }
            assertEquals(HttpStatusCode.OK, response.status)

            val nullableList = response.body<NullableListModel>()
            assertEquals(listOf("a", "b", "c"), nullableList.requiredList)
            assertNull(nullableList.nullableList)
        }
    }

    @Test
    fun testMissingRequiredList() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { json() }
                route<NullableListModel>("/nullable-list") { value ->
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val response = client.get("/nullable-list") {
                // Not providing requiredList which is required
                url.parameters.appendAll("nullableList", listOf("x", "y", "z"))
            }
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }
}
