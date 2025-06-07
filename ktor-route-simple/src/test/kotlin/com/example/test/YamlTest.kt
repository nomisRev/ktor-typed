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
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlNode
import io.ktor.route.simple.yaml

@Serializable
data class Person(val name: String, val age: Int)

@Serializable
data class StringWrapper(val value: String)

@Serializable
data class StringYamlTest(@Body val body: StringWrapper)

@Serializable
data class PersonYamlTest(@Body val body: Person)

@Serializable
data class ListYamlTest(@Body val body: List<String>)

class YamlTest {
    @Test
    fun testYamlStringAsBody() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { yaml() }
                get<StringYamlTest>("/yaml-string-body") { value ->
                    // Echo back the received YAML string
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { yaml() }
            }

            val stringWrapper = StringWrapper("Hello World")

            val response = client.get("/yaml-string-body") {
                contentType(ContentType.Application.Yaml)
                setBody(stringWrapper)
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<StringYamlTest>()
            assertEquals(StringYamlTest(stringWrapper), responseBody)
        }
    }

    @Test
    fun testYamlPersonAsBody() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { yaml() }
                get<PersonYamlTest>("/yaml-person-body") { value ->
                    // Echo back the received person
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { yaml() }
            }

            val person = Person("John", 30)

            val response = client.get("/yaml-person-body") {
                contentType(ContentType.Application.Yaml)
                setBody(person)
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<PersonYamlTest>()
            assertEquals(PersonYamlTest(person), responseBody)
        }
    }

    @Test
    fun testYamlListAsBody() {
        testApplication {
            routing {
                install(ServerContentNegotiation) { yaml() }
                get<ListYamlTest>("/yaml-list-body") { value ->
                    // Echo back the received list
                    call.respond(HttpStatusCode.OK, value)
                }
            }

            val client = createClient {
                install(ContentNegotiation) { yaml() }
            }

            val listBody = listOf("item1", "item2", "item3")

            val response = client.get("/yaml-list-body") {
                contentType(ContentType.Application.Yaml)
                setBody(listBody)
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = response.body<ListYamlTest>()
            assertEquals(ListYamlTest(listBody), responseBody)
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
        val map = yaml.decodeFromString(YamlNode.serializer(), yamlString).toString()

        // Verify the parsed content
        assert(map.contains("John"))
        assert(map.contains("30"))
        assert(map.contains("true"))
        assert(map.contains("123 Main St"))
        assert(map.contains("reading"))
    }
}
