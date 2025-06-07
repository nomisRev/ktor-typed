package com.example.test

import de.infix.testBalloon.framework.testSuite
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

val ListSpec by testSuite {
    testRoute<ListPrimitives>("List of primitives") {
        val response = client.get("/list-primitives") {
            url.parameters.appendAll("stringList", listOf("a", "b", "c"))
            url.parameters.appendAll("intList", listOf("1", "2", "3"))
            url.parameters.appendAll("longList", listOf("1000000000000", "2000000000000", "3000000000000"))
            url.parameters.appendAll("doubleList", listOf("1.1", "2.2", "3.3"))
            url.parameters.appendAll("floatList", listOf("1.1", "2.2", "3.3"))
            url.parameters.appendAll("booleanList", listOf("true", "false", "true"))
            url.parameters.appendAll("byteList", listOf("1", "2", "3"))
            url.parameters.appendAll("charList", listOf("a", "b", "c"))
            url.parameters.appendAll("shortList", listOf("1", "2", "3"))
        }
        val expected = ListPrimitives(
            stringList = listOf("a", "b", "c"),
            intList = listOf(1, 2, 3),
            longList = listOf(1000000000000L, 2000000000000L, 3000000000000L),
            doubleList = listOf(1.1, 2.2, 3.3),
            floatList = listOf(1.1f, 2.2f, 3.3f),
            booleanList = listOf(true, false, true),
            byteList = listOf(1.toByte(), 2.toByte(), 3.toByte()),
            charList = listOf('a', 'b', 'c'),
            shortList = listOf(1.toShort(), 2.toShort(), 3.toShort()),
        )
        assert(HttpStatusCode.OK == response.status)
        assert(expected == response.body<ListPrimitives>())
    }

    testRoute<ListPrimitives>("Required List missing") {
        val response = client.get("/list-primitives") {
            url.parameters.appendAll("stringList", listOf("a", "b", "c"))
            url.parameters.appendAll("intList", listOf("1", "2", "3"))
            url.parameters.appendAll("longList", listOf("1000000000000", "2000000000000", "3000000000000"))
            url.parameters.appendAll("doubleList", listOf("1.1", "2.2", "3.3"))
            url.parameters.appendAll("floatList", listOf("1.1", "2.2", "3.3"))
            url.parameters.appendAll("booleanList", listOf("true", "false", "true"))
            url.parameters.appendAll("byteList", listOf("1", "2", "3"))
            url.parameters.appendAll("charList", listOf("a", "b", "c"))
        }
        assert(HttpStatusCode.BadRequest == response.status)
    }
}
