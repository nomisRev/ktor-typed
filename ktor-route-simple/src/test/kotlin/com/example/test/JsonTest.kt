package com.example.test

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.route.simple.Body
import io.ktor.route.simple.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class JsonElementBody(
    @Body val jsonBody: JsonElement
)

@Serializable
data class MapStringBody(
    @Body val mapBody: Map<String, String>
)

@Serializable
data class JsonObjectBody(
    @Body val jsonObjectBody: JsonObject
)

@Serializable
data class JsonArrayBody(
    @Body val jsonArrayBody: JsonArray
)

@Serializable
data class JsonPrimitiveBody(
    @Body val jsonPrimitiveBody: JsonPrimitive
)

class JsonTest {
    @Test
    fun testJsonElementAsBody() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/json-element-body") { value: JsonElementBody ->
                        // Echo back the received JsonElement
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val jsonObject = buildJsonObject {
                put("name", JsonPrimitive("John"))
                put("age", JsonPrimitive(30))
            }

            val response = client.get("/json-element-body") {
                contentType(ContentType.Application.Json)
                setBody(jsonObject)
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<JsonElementBody>()
            assertEquals(jsonObject, responseBody.jsonBody)
        }
    }

    @Test
    fun testMapStringStringAsBody() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/map-string-body") { value: MapStringBody ->
                        // Echo back the received Map
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val mapBody = mapOf(
                "name" to "John",
                "email" to "john@example.com",
                "role" to "admin"
            )

            val response = client.get("/map-string-body") {
                contentType(ContentType.Application.Json)
                setBody(mapBody)
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<MapStringBody>()
            assertEquals(mapBody, responseBody.mapBody)
        }
    }

    @Test
    fun testJsonObjectAsBody() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/json-object-body") { value: JsonObjectBody ->
                        // Echo back the received JsonObject
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val jsonObject = buildJsonObject {
                put("name", JsonPrimitive("John"))
                put("age", JsonPrimitive(30))
                put("isAdmin", JsonPrimitive(true))
            }

            val response = client.get("/json-object-body") {
                contentType(ContentType.Application.Json)
                setBody(jsonObject)
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<JsonObjectBody>()
            assertEquals(jsonObject, responseBody.jsonObjectBody)
        }
    }

    @Test
    fun testJsonArrayAsBody() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/json-array-body") { value: JsonArrayBody ->
                        // Echo back the received JsonArray
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val jsonArray = buildJsonArray {
                add(JsonPrimitive("item1"))
                add(JsonPrimitive(42))
                add(JsonPrimitive(true))
                add(buildJsonObject {
                    put("nestedKey", JsonPrimitive("nestedValue"))
                })
            }

            val response = client.get("/json-array-body") {
                contentType(ContentType.Application.Json)
                setBody(jsonArray)
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<JsonArrayBody>()
            assertEquals(jsonArray, responseBody.jsonArrayBody)
        }
    }

    @Test
    fun testJsonPrimitiveAsBody() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/json-primitive-body") { value: JsonPrimitiveBody ->
                        // Echo back the received JsonPrimitive
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val jsonPrimitive = JsonPrimitive("Simple string value")

            val response = client.get("/json-primitive-body") {
                contentType(ContentType.Application.Json)
                setBody(jsonPrimitive)
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<JsonPrimitiveBody>()
            assertEquals(jsonPrimitive, responseBody.jsonPrimitiveBody)
        }
    }

    @Test
    fun testWrongBodyTypeForJsonObject() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/json-object-body") { value: JsonObjectBody ->
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            // Send a JsonArray instead of a JsonObject
            val jsonArray = buildJsonArray {
                add(JsonPrimitive("item1"))
                add(JsonPrimitive(42))
            }

            val response = client.get("/json-object-body") {
                contentType(ContentType.Application.Json)
                setBody(jsonArray)
            }

            // Should result in a BadRequest
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun testWrongBodyTypeForJsonArray() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/json-array-body") { value: JsonArrayBody ->
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            // Send a JsonObject instead of a JsonArray
            val jsonObject = buildJsonObject {
                put("name", JsonPrimitive("John"))
                put("age", JsonPrimitive(30))
            }

            val response = client.get("/json-array-body") {
                contentType(ContentType.Application.Json)
                setBody(jsonObject)
            }

            // Should result in a BadRequest
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun testWrongBodyTypeForJsonPrimitive() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/json-primitive-body") { value: JsonPrimitiveBody ->
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            // Send a JsonObject instead of a JsonPrimitive
            val jsonObject = buildJsonObject {
                put("name", JsonPrimitive("John"))
            }

            val response = client.get("/json-primitive-body") {
                contentType(ContentType.Application.Json)
                setBody(jsonObject)
            }

            // Should result in a BadRequest
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun testWrongBodyTypeForMapString() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/map-string-body") { value: MapStringBody ->
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            // Send a JsonArray instead of a Map<String, String>
            val jsonArray = buildJsonArray {
                add(JsonPrimitive("item1"))
                add(JsonPrimitive(42))
            }

            val response = client.get("/map-string-body") {
                contentType(ContentType.Application.Json)
                setBody(jsonArray)
            }

            // Should result in a BadRequest
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun testMalformedJsonBody() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/json-element-body") { value: JsonElementBody ->
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            // Send malformed JSON
            val response = client.get("/json-element-body") {
                contentType(ContentType.Application.Json)
                setBody("{invalid json")
            }

            // Should result in a BadRequest
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }
}
