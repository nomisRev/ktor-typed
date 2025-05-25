package com.example

import com.example.Path
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.ApplicationReceivePipeline
import io.ktor.server.response.ApplicationSendPipeline
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.pipeline.PipelinePhase

fun main(args: Array<String>) =
    EngineMain.main(args)

data class User(val name: String, val age: Int, val email: String, val custom: Int, val sessionId: String)

val route = Path { "a" / string("name") / "b" / int("age") }
    .query(Parameter.Query("email", false) { it })
    .header(Parameter.Header("X-Custom-Header", true) { it.toInt() })
    .cookie(Parameter.Cookie("session_id", false) { it })
    .map { (name, age, email, custom, sessionId) -> User(name, age, email, custom, sessionId) }

fun Application.module() {
    install(CallLogging)
    routing {
        get(route) { user ->
            call.respond(HttpStatusCode.OK, "Hello $user!")
        }
    }
}
