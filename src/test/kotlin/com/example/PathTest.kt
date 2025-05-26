package com.example

import com.example.codec.Codec
import io.ktor.client.statement.bodyAsText
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import kotlin.test.Test

class RouteTest {

    // @Serializable can this help provide transformation between User -> Params5 ???
    data class User(
        val name: String,
        val age: Int,
        val email: String,
        val custom: List<String>,
        val sessionId: String,
    ) {
        // TODO: how to get rid of this boilerplate...
        fun toParams(): Params5<String, Int, String, List<String>, String> =
            Params5(name, age, email, custom, sessionId)
    }

    @Test
    fun test() = testApplication {
        val route = Path { "a" / string("name") / "b" / int("age") }
            .query(Parameter.Query("email", false, Codec.string))
            .header(Parameter.Header("X-Custom-Header", true))
            .cookie(Parameter.Cookie("session_id", false))
            .map(::User, User::toParams)
          //.to<User>() - compile time function??

        routing {
            get(route) { (name, age, email, header, session) ->
                call.respond("($name, $age, $email, $header, $session)")
            }
        }
        val response = client.get(route, User("John", 21, "j.d@jb.com", listOf("header"), "session"))
        assert(response.bodyAsText() == "(John, 21, j.d@jb.com, [header], session)")
    }
}
