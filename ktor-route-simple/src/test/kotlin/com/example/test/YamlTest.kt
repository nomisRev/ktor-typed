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
import kotlin.test.Test
import kotlin.test.assertEquals
import com.charleskorn.kaml.Yaml

@Serializable
data class YamlStringBody(
    @Body val yamlBody: String
)

@Serializable
data class YamlMapBody(
    @Body val mapBody: Map<String, String>
)

@Serializable
data class YamlListBody(
    @Body val listBody: List<String>
)

class YamlTest {
    @Test
    fun testYamlStringAsBody() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/yaml-string-body") { value: YamlStringBody ->
                        // Echo back the received YAML string
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val yamlString = """
                name: John
                age: 30
            """.trimIndent()

            val response = client.get("/yaml-string-body") {
                contentType(ContentType.Application.Json)
                setBody(YamlStringBody(yamlString))
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<YamlStringBody>()
            assertEquals(yamlString, responseBody.yamlBody)
        }
    }

    @Test
    fun testMapStringStringAsBody() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/yaml-map-body") { value: YamlMapBody ->
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

            val response = client.get("/yaml-map-body") {
                contentType(ContentType.Application.Json)
                setBody(YamlMapBody(mapBody))
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<YamlMapBody>()
            assertEquals(mapBody, responseBody.mapBody)
        }
    }

    @Test
    fun testYamlListAsBody() {
        testApplication {
            application {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get("/yaml-list-body") { value: YamlListBody ->
                        // Echo back the received list
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }

            val client = createClient {
                install(ContentNegotiation) { json() }
            }

            val listBody = listOf("item1", "item2", "item3")

            val response = client.get("/yaml-list-body") {
                contentType(ContentType.Application.Json)
                setBody(YamlListBody(listBody))
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<YamlListBody>()
            assertEquals(listBody, responseBody.listBody)
        }
    }

    @Test
    fun testParsingYamlWithKaml() {
        // This test demonstrates using the Kaml library to parse YAML
        val yaml = Yaml.default
        val yamlString = """
            name: John
            age: 30
            isAdmin: true
            address:
              street: 123 Main St
              city: Anytown
            hobbies:
              - reading
              - hiking
              - coding
        """.trimIndent()

        // Parse YAML to a Map
        val map = yaml.decodeFromString(kotlinx.serialization.json.JsonElement.serializer(), yamlString).toString()

        // Verify the parsed content
        assert(map.contains("John"))
        assert(map.contains("30"))
        assert(map.contains("true"))
        assert(map.contains("123 Main St"))
        assert(map.contains("reading"))
    }
}
