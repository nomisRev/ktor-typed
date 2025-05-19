package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.response.respond
import io.ktor.server.routing.routing

fun main(args: Array<String>) =
    EngineMain.main(args)

data class User(val name: String, val age: Int, val email: String)

fun Application.module() = routing {
    Path { "a" / string("name") / "c" / int("age") }
        .get()
        .query(QueryParameter("email", { it }, false))
        .map { (name, age, email) -> User(name, age, email) }
        .handle { user ->
            call.respond(HttpStatusCode.OK, "Hello $user!")
        }
}
