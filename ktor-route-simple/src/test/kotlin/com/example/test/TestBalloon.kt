package com.example.test

import de.infix.testBalloon.framework.AbstractTest
import de.infix.testBalloon.framework.TestCoroutineScope
import de.infix.testBalloon.framework.TestSuite
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.ktor.route.simple.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.route.simple.yaml
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.ClientProvider
import io.ktor.server.testing.TestApplicationBuilder
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.KtorDsl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

inline fun <reified A : Any> TestSuite.testRoute(
    name: String,
    path: String = "/route",
    crossinline action: suspend KtorTestApplicationScope.() -> Unit
) = testServer(name) {
    routing {
        install(ServerContentNegotiation) {
            json()
            yaml()
        }
        get<A>(path) { value ->
            val contentType = call.request.headers["Content-Type"]
            when {
                contentType == null -> call.respond(HttpStatusCode.OK, value)
                ContentType.Application.Json.match(contentType) -> call.respondText(
                    Json.encodeToString(serializer(), value),
                    ContentType.Application.Json,
                    HttpStatusCode.OK,
                )
                ContentType.Application.Yaml.match(contentType) -> call.respondText(
                    Json.encodeToString(serializer(), value),
                    ContentType.Application.Yaml,
                    HttpStatusCode.OK,
                )
                else -> call.respond(HttpStatusCode.OK, value)
            }
        }
    }
    action()
}

fun TestSuite.testServer(
    name: String,
    action: suspend KtorTestApplicationScope.() -> Unit
) = test(name) scope@{
    testApplication app@{
        with(KtorTestApplicationScope(this@scope, this@app)) {
            action()
        }
    }
}

open class KtorTestApplicationScope internal constructor(
    private val test: TestCoroutineScope,
    private val original: ApplicationTestBuilder,
) : AbstractTest by test,
    CoroutineScope by test {

    val testScope: TestScope
        get() = test.testScope

    val client: HttpClient by lazy {
        original.createClient {
            install(ContentNegotiation) {
                json()
                yaml()
            }
        }
    }

    fun routing(configuration: io.ktor.server.routing.Route.() -> Unit) {
        original.routing(configuration)
    }

    @KtorDsl
    fun createClient(
        block: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit
    ): HttpClient = original.createClient(block)
}
