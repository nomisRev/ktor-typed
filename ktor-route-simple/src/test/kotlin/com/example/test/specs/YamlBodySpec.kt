package com.example.test.specs

import com.example.test.Body
import com.example.test.CreateUser
import com.example.test.ListYamlTest
import com.example.test.Person
import com.example.test.PersonYamlTest
import com.example.test.StringWrapper
import com.example.test.StringYamlTest
import com.example.test.testRoute
import de.infix.testBalloon.framework.testSuite
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

val YamlBodySpec by testSuite {
    testRoute<StringYamlTest>("YAML string as body", "/yaml-string-body") {
        val stringWrapper = StringWrapper("Hello World")

        val response = client.get("/yaml-string-body") {
            contentType(ContentType.Application.Yaml)
            setBody(stringWrapper)
        }

        val expected = StringYamlTest(body = stringWrapper)
        assert(expected == response.body<StringYamlTest>())
    }

    testRoute<PersonYamlTest>("YAML person as body", "/yaml-person-body") {
        val person = Person("John", 30)

        val response = client.get("/yaml-person-body") {
            contentType(ContentType.Application.Yaml)
            setBody(person)
        }

        val expected = PersonYamlTest(body = person)
        assert(expected == response.body<PersonYamlTest>())
    }

    testRoute<ListYamlTest>("YAML list as body", "/yaml-list-body") {
        val list = listOf("item1", "item2", "item3")

        val response = client.get("/yaml-list-body") {
            contentType(ContentType.Application.Yaml)
            setBody(list)
        }

        val expected = ListYamlTest(body = list)
        assert(expected == response.body<ListYamlTest>())
    }

    testRoute<CreateUser>("CreateUser with YAML", "/users/{userId}/create") {
        val response = client.get("/users/123/create?name=John&age=30") {
            headers.append("X-flag", "true")
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Yaml)
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

    testRoute<CreateUser>("Missing header YAML", "/users/{userId}/create") {
        val response = client.get("/users/123/create?name=John&age=30") {
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Yaml)
            setBody(Body("Hello World"))
        }
        assert(HttpStatusCode.BadRequest == response.status)
    }

    testRoute<CreateUser>("Missing query YAML", "/users/{userId}/create") {
        val response = client.get("/users/123/create?name=John") {
            headers.append("X-flag", "true")
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Yaml)
            setBody(Body("Hello World"))
        }
        assert(HttpStatusCode.BadRequest == response.status)
    }

    testRoute<CreateUser>("Missing body YAML", "/users/{userId}/create") {
        val response = client.get("/users/123/create?name=John&age=30") {
            headers.append("X-flag", "true")
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Yaml)
        }
        assert(HttpStatusCode.BadRequest == response.status)
    }

    testRoute<CreateUser>("Missing path YAML", "/users/{userId}/create") {
        val response = client.get("/users/123/create?name=John") {
            headers.append("X-flag", "true")
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Yaml)
            setBody(Body("Hello World"))
        }
        assert(HttpStatusCode.BadRequest == response.status)
    }
}