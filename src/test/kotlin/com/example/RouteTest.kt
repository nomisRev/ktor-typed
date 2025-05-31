package com.example

import com.example.codec.Codec
import com.example.Parameter.Query
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RouteTest {

    data class BasicUser(
        val name: String,
        val age: Int
    )

    data class ComplexUser(
        val name: String,
        val age: Int?,
        val email: String?,
        val test: Boolean,
        val custom: String,
        val sessionId: String
    )

    @Test
    fun testBasicPathParameters() {
        val route = Path { "users" / string("name") / int("age") }
            .asDataClass(::BasicUser)

        val user = BasicUser("John", 30)

        testInput(route, user)
    }

    @Test
    fun testMixedRequiredAndOptionalParameters() {
        val route = Path { "users" / string("name") / intOrNull("age") }
            .query(Query.stringOrNull("email"))
            .query(Query.boolean("test"))
            .header("X-Custom-Header")
            .cookie(Parameter.Cookie("session_id"))
            .asDataClass(::ComplexUser)

        val user = ComplexUser("John", 21, "j.d@jb.com", true, "header", "session")

        testInput(route, user)
    }

    @Test
    fun testMultipleHeadersAndCookies() {
        data class AuthData(
            val userId: String,
            val clientId: String,
            val userAgent: String,
            val acceptLanguage: String,
            val sessionToken: String,
            val refreshToken: String
        )

        val route = Path { "auth" / string("userId") }
            .query(Parameter.Query("client_id", Codec.string))
            .header("User-Agent")
            .header("Accept-Language")
            .cookie(Parameter.Cookie("session_token"))
            .cookie(Parameter.Cookie("refresh_token"))
            .asDataClass(::AuthData)

        val authData = AuthData(
            userId = "user123",
            clientId = "client456",
            userAgent = "Mozilla/5.0",
            acceptLanguage = "en-US,en;q=0.9",
            sessionToken = "session789",
            refreshToken = "refresh012"
        )

        testInput(route, authData)
    }

    @Test
    fun testDifferentPathParameterTypes() {
        data class PathParams(
            val stringParam: String,
            val intParam: Int,
            val longParam: Long
        )

        val route = Path {
            "api" / string("stringParam") / int("intParam") / long("longParam")
        }.asDataClass(::PathParams)

        val params = PathParams(
            stringParam = "test",
            intParam = 42,
            longParam = 9223372036854775807
        )

        testInput(route, params)
    }

    @Test
    fun testQueryParameters() {
        data class QueryParams(
            val type: String,
            val name: String,
            val count: Int
        )

        val route = Path { "query" / string("type") }
            .query(Query.string("name"))
            .query(Parameter.Query("count", Codec.int))
            .asDataClass(::QueryParams)

        val params = QueryParams(
            type = "test",
            name = "example",
            count = 42
        )

        testInput(route, params)
    }

    @Test
    fun testComplexPathStructure() {
        data class ResourceData(
            val apiVersion: String,
            val resourceType: String,
            val resourceId: Long,
            val subResourceType: String,
            val subResourceId: Int,
            val format: String,
            val includeMetadata: Boolean
        )

        val route = Path {
            "api" / string("apiVersion") / string("resourceType") / long("resourceId") /
                    string("subResourceType") / int("subResourceId") / string("format")
        }
            .query(Parameter.Query("include_metadata", Codec.boolean))
            .asDataClass(::ResourceData)

        val resourceData = ResourceData(
            apiVersion = "v2",
            resourceType = "users",
            resourceId = 12345L,
            subResourceType = "posts",
            subResourceId = 67890,
            format = "json",
            includeMetadata = true
        )

        testInput(route, resourceData)
    }
}

fun <A> testInput(
    route: Route<A, Unit>,
    expected: A,
    methods: List<HttpMethod> = HttpMethod.DefaultMethods,
    assert: (actual: A) -> Unit = { actual -> assert(actual == expected) }
) = testApplication {
    routing {
        methods.forEach { method ->
            route.handle(method) { actual ->
                assert(actual)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
    methods.forEach { method ->
        val response = client.request(method, route, expected)
        try {
            assert(response.status == HttpStatusCode.OK)
        } catch (e: AssertionError) {
            throw AssertionError("""
            |${route.path.routeString()} failed for method $method:
            |
            |${e.message}""".trimMargin())
        }
    }
}
