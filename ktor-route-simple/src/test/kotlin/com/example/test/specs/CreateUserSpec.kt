package com.example.test.specs

import com.example.test.Body
import com.example.test.CreateUser
import com.example.test.testRoute
import de.infix.testBalloon.framework.testSuite
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

val CreateUserSpec by testSuite {
    testRoute<CreateUser>("CreateUser test", "/users/{userId}/create") {
        val response = client.get("/users/123/create?name=John&age=30") {
            headers.append("X-flag", "true")
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Json)
            setBody(Body("Hello World"))
        }
        val expected = CreateUser(
            userId = "123",
            NAME = "John",
            age = 30,
            many = listOf("a", "b", "c"),
            header = true,
            body = Body("Hello World")
        )
        assert(expected == response.body<CreateUser>())
    }

    testRoute<CreateUser>("Missing header", "/users/{userId}/create") {
        val response = client.get("/users/123/create?name=John&age=30") {
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Json)
            setBody(Body("Hello World"))
        }
        assert(HttpStatusCode.BadRequest == response.status)
    }

    testRoute<CreateUser>("Missing query", "/users/{userId}/create") {
        val response = client.get("/users/123/create?name=John") {
            headers.append("X-flag", "true")
            contentType(ContentType.Application.Json)
            setBody(Body("Hello World"))
        }
        assert(HttpStatusCode.BadRequest == response.status)
    }

    testRoute<CreateUser>("Missing body", "/users/{userId}/create") {
        val response = client.get("/users/123/create?name=John&age=30") {
            headers.append("X-flag", "true")
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Json)
        }
        assert(HttpStatusCode.BadRequest == response.status)
    }

    testRoute<CreateUser>("Missing path", "/users/{userId}/create") {
        val response = client.get("/users/123/create?name=John") {
            headers.append("X-flag", "true")
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Json)
            setBody(Body("Hello World"))
        }
        assert(HttpStatusCode.BadRequest == response.status)
    }
}