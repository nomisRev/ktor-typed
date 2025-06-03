package com.example.test


import de.infix.testBalloon.framework.testSuite

val routeSpec by testSuite {
    test("success") {
        throw RuntimeException("boom!")
    }
}

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
