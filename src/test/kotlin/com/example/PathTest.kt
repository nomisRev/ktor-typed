package com.example

import io.ktor.client.statement.bodyAsText
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class PathTest {

    @Test
    fun test() = testApplication {
        val route = Path { "a" / string("name") / "b" / int("age") }
        routing {
            get(route) { (name, age) ->
                call.respond("($name, $age)")
            }
        }
        assert(client.get(route, Params2("John", 21)).bodyAsText() == "(John, 21)")
    }
}
