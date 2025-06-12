package com.example.test.specs

import com.example.test.TwoBodies
import de.infix.testBalloon.framework.testSuite
import io.ktor.route.simple.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import io.ktor.http.HttpStatusCode

val ErrorHandlingSpec by testSuite {
    test("Two bodies") {
        try {
            testApplication {
                routing {
                    install(ServerContentNegotiation) { json() }
                    get<TwoBodies>("/two-bodies") { value ->
                        call.respond(HttpStatusCode.OK, value)
                    }
                }
            }
            assert(false) { "Expected IllegalArgumentException to be thrown" }
        } catch (e: IllegalArgumentException) {
            val expectedMessage = "Only a single or no @Body annotation is allowed but found 'one', 'two' in TwoBodies"
            assert(expectedMessage == e.message)
        }
    }
}