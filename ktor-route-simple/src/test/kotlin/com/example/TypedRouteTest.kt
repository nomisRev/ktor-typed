package com.example.test

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.route.simple.Body
import io.ktor.route.simple.Header
import io.ktor.route.simple.route
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.annotations.Blocking
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class CreateUser(
    val userId: String,
    @SerialName("name") val NAME: String,
    val age: Int,
    val many: List<String>,
    @Header("X-flag")
    val header: Boolean,
    @Body val body: JsonBody
)

@Serializable
data class JsonBody(val value: String)

class MyTest {
    @Test
    fun test() = withRoute<CreateUser>("/users/{userId}/create") {
        val user = get("/users/123/create?name=John&age=30") {
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

    @Test
    fun missingHeader() = withRoute<CreateUser>("/users/{userId}/create") {
        val response = get("/users/123/create?name=John&age=30") {
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Json)
            setBody(JsonBody("Hello World"))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun missingQuery() = withRoute<CreateUser>("/users/{userId}/create") {
        val response = get("/users/123/create?name=John&age=30") {
            headers.append("X-flag", "true")
            contentType(ContentType.Application.Json)
            setBody(JsonBody("Hello World"))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun missingBody() = withRoute<CreateUser>("/users/{userId}/create") {
        val response = get("/users/123/create?name=John&age=30") {
            headers.append("X-flag", "true")
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Json)
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun missingPath() = withRoute<CreateUser>("/users/{userId}/create") {
        val response = get("/users/123/create?name=John") {
            headers.append("X-flag", "true")
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Json)
            setBody(JsonBody("Hello World"))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Serializable
    data class TwoBodies(@Body val one: JsonBody, @Body val two: String)

    @Test
    fun twoBodies() {
        val message = assertThrows<IllegalArgumentException> {
            withRoute<TwoBodies> {}
        }.message

        assertEquals(
            "Only a single or no @Body annotation is allowed but found 'one', 'two' in TwoBodies",
            message
        )
    }
}

inline fun <reified A : Any> withRoute(
    path: String = "/users/{userId}/create",
    crossinline block: suspend HttpClient.() -> Unit
) = testApplication {
    routing {
        install(ServerContentNegotiation) { json() }
        route(path) { value: A ->
            call.respond(HttpStatusCode.OK, value)
        }
    }
    startApplication()
    block(createClient {
        install(ContentNegotiation) { json() }
    })
}

