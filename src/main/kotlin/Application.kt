package com.example

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main(args: Array<String>) =
    EngineMain.main(args)

data class User(val name: String, val age: Int)

fun Application.module() = routing {
    val p = Path { "a" / param<String>("name") / "c" / param<Int>("age") }
        .map { (name, age) -> User(name, age) }

    route(p.routeString(), HttpMethod.Get) {
        handle {
            val age = call.parameters["age"]
            val name = call.parameters["name"]
            val map = p.transform as (Pair<String, Int>) -> User
            val user = map(Pair(name!!, age!!.toInt()))
            call.respond(HttpStatusCode.OK, "Hello $user!")
        }
    }
}
