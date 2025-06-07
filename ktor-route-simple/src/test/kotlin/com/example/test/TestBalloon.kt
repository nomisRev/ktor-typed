package com.example.test

import de.infix.testBalloon.framework.AbstractTest
import de.infix.testBalloon.framework.TestCoroutineScope
import de.infix.testBalloon.framework.TestSuite
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.route.simple.get
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.respond
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.ClientProvider
import io.ktor.server.testing.TestApplicationBuilder
import io.ktor.server.testing.testApplication
import io.ktor.utils.io.KtorDsl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope

inline fun <reified A : Any> TestSuite.testRoute(
    name: String,
    crossinline action: suspend KtorTestApplicationScope.() -> Unit
) = testServer(name) {
    routing {
        get("/route") { value: A ->
            call.respond(HttpStatusCode.OK, value)
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
) : TestApplicationBuilder(),
    ClientProvider,
    AbstractTest by test,
    CoroutineScope by test {

    val testScope: TestScope
        get() = test.testScope

    override val client: HttpClient by lazy {
        createClient {
            install(ContentNegotiation) { json() }
        }
    }

    suspend fun startApplication() = original.startApplication()

    @KtorDsl
    override fun createClient(
        block: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit
    ): HttpClient = original.createClient(block)
}
