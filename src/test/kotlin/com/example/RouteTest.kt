package com.example

import com.example.codec.Codec
import io.ktor.client.statement.bodyAsText
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication

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

//val routeSpec by testSuite {
//    testServer("test") {
//        val route = Path { "a" / string("name") / "b" / int("age") }
//            .query(Parameter.Query("email", Codec.string))
//            .header(Parameter.Header("X-Custom-Header"))
//            .cookie(Parameter.Cookie("session_id"))
//            .map(::User, User::toParams)
//        //.to<User>() - compile time function??
//
//        routing {
//            get(route) { (name, age, email, header, session) ->
//                call.respond("($name, $age, $email, $header, $session)")
//            }
//        }
//
//        val response = client.get(route, User("John", 21, "j.d@jb.com", listOf("header"), "session"))
//        assert(response.bodyAsText() == "(John, 21, j.d@jb.com, [header], session)")
//        throw RuntimeException("BOOM!")
//    }
//}
//
//fun TestSuite.testServer(
//    name: String,
//    action: suspend KtorTestApplicationScope.() -> Unit
//) = test(name) scope@{
//    testApplication app@{
//        with(KtorTestApplicationScope(this@scope, this@app)) {
//            action()
//        }
//    }
//}
//
//open class KtorTestApplicationScope internal constructor(
//    private val test: TestCoroutineScope,
//    private val original: ApplicationTestBuilder,
//) : TestApplicationBuilder(),
//    ClientProvider,
//    AbstractTest by test,
//    CoroutineScope by test {
//
//    val testScope: TestScope
//        get() = test.testScope
//
//    override val client: HttpClient by lazy {
//        createClient {
//            install(ContentNegotiation) { json() }
//        }
//    }
//
//    suspend fun startApplication() = original.startApplication()
//
//    @KtorDsl
//    override fun createClient(
//        block: HttpClientConfig<out HttpClientEngineConfig>.() -> Unit
//    ): HttpClient = original.createClient(block)
//}
