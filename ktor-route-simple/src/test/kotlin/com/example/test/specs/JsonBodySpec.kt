package com.example.test.specs

import com.example.test.JsonArrayBody
import com.example.test.JsonElementBody
import com.example.test.JsonObjectBody
import com.example.test.JsonPrimitiveBody
import com.example.test.MapStringBody
import com.example.test.testRoute
import de.infix.testBalloon.framework.testSuite
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

val JsonBodySpec by testSuite {
    testRoute<JsonElementBody>("JsonElement as body", "/json-element-body") {
        val jsonElement = buildJsonObject {
            put("name", JsonPrimitive("John"))
            put("age", JsonPrimitive(30))
        }

        val response = client.get("/json-element-body") {
            contentType(ContentType.Application.Json)
            setBody(jsonElement)
        }

        val expected = JsonElementBody(body = jsonElement)
        assert(expected == response.body<JsonElementBody>())
    }

    testRoute<MapStringBody>("Map<String, String> as body", "/map-string-body") {
        val mapBody = mapOf("key1" to "value1", "key2" to "value2")

        val response = client.get("/map-string-body") {
            contentType(ContentType.Application.Json)
            setBody(mapBody)
        }

        val expected = MapStringBody(mapBody = mapBody)
        assert(expected == response.body<MapStringBody>())
    }

    testRoute<JsonObjectBody>("JsonObject as body", "/json-object-body") {
        val jsonObject = buildJsonObject {
            put("name", JsonPrimitive("Alice"))
            put("age", JsonPrimitive(25))
        }

        val response = client.get("/json-object-body") {
            contentType(ContentType.Application.Json)
            setBody(jsonObject)
        }

        val expected = JsonObjectBody(jsonObjectBody = jsonObject)
        assert(expected == response.body<JsonObjectBody>())
    }

    testRoute<JsonArrayBody>("JsonArray as body", "/json-array-body") {
        val jsonArray = buildJsonArray {
            add(JsonPrimitive("item1"))
            add(JsonPrimitive("item2"))
            add(JsonPrimitive("item3"))
        }

        val response = client.get("/json-array-body") {
            contentType(ContentType.Application.Json)
            setBody(jsonArray)
        }

        val expected = JsonArrayBody(jsonArrayBody = jsonArray)
        assert(expected == response.body<JsonArrayBody>())
    }

    testRoute<JsonPrimitiveBody>("JsonPrimitive as body", "/json-primitive-body") {
        val jsonPrimitive = JsonPrimitive("Hello, World!")

        val response = client.get("/json-primitive-body") {
            contentType(ContentType.Application.Json)
            setBody(jsonPrimitive)
        }

        val expected = JsonPrimitiveBody(jsonPrimitiveBody = jsonPrimitive)
        assert(expected == response.body<JsonPrimitiveBody>())
    }
}