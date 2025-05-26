package com.example

import com.example.codec.Codec
import io.ktor.client.statement.bodyAsText
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RouteTest {

    @Test
    fun test() = testApplication {
        val route = Path { "a" / string("name") / "b" / int("age") }
            .query(Parameter.Query("email", false, Codec.string) )
            .header(Parameter.Header("X-Custom-Header", true))
            .cookie(Parameter.Cookie("session_id", false))

        routing {
            get(route) { (name, age, email, header, session) ->
                call.respond("($name, $age, $email, $header, $session)")
            }
        }
        val response = client.get(route, Params5("John", 21, "j.d@jb.com", listOf("header"), "session"))
        assert(response.bodyAsText() == "(John, 21, j.d@jb.com, [header], session)")
    }
}
