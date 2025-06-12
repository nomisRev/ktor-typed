package com.example.test.specs

import com.example.test.Simple
import com.example.test.testRoute
import de.infix.testBalloon.framework.testSuite
import io.ktor.client.call.body
import io.ktor.client.request.get

val SimpleRouteSpec by testSuite {
    testRoute<Simple>("Simple") {
        val response = client.get("/route?name=John&age=32")
        val expected = Simple("John", 32)
        assert(expected == response.body<Simple>())
    }
}