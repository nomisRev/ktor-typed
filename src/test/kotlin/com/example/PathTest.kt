package com.example

import com.example.codec.Codec
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RouteTest {

    data class User(
        val name: String,
        val age: Int,
        val email: String?,
        val custom: List<String>,
        val sessionId: String,
    )

    @Test
    fun test() {
        val route = Path { "a" / string("name") / "b" / int("age") }
            .query(Parameter.Query("email", Codec.stringOrNull))
            .header(Parameter.Header("X-Custom-Header"))
            .cookie(Parameter.Cookie("session_id"))
            .asDataClass(::User)

        val user = User("John", 21, "j.d@jb.com", listOf("header"), "session")

        testInput(route, user)
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
            route.handle(method) { a ->
                assert(a)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
    methods.forEach { method ->
        val response = client.request(method, route, expected)
        assert(response.status == HttpStatusCode.OK)
    }
}
