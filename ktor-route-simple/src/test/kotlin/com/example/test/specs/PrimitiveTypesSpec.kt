package com.example.test.specs

import com.example.test.AllPrimitives
import com.example.test.ListPrimitives
import com.example.test.NullableBody
import com.example.test.NullableHeader
import com.example.test.testRoute
import de.infix.testBalloon.framework.testSuite
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

val PrimitiveTypesSpec by testSuite {
    // List Primitives Tests
    testRoute<ListPrimitives>("List of primitives") {
        val response = client.get("/route") {
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
        assert(expected == response.body<ListPrimitives>())
    }

    testRoute<ListPrimitives>("Required List missing") {
        val response = client.get("/route") {
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

    // Primitive Types Tests
    testRoute<AllPrimitives>("All primitives") {
        val response = client.get("/route?stringValue=test&intValue=42&longValue=9223372036854775807&doubleValue=3.14159&floatValue=2.71828&booleanValue=true&byteValue=127&charValue=A&shortValue=32767&nullableStringValue=nullable&nullableIntValue=43&nullableLongValue=9223372036854775806&nullableDoubleValue=1.618&nullableFloatValue=0.577&nullableBooleanValue=false&nullableByteValue=126&nullableCharValue=B&nullableShortValue=32766")
        val expected = AllPrimitives(
            stringValue = "test",
            intValue = 42,
            longValue = 9223372036854775807L,
            doubleValue = 3.14159,
            floatValue = 2.71828f,
            booleanValue = true,
            byteValue = 127.toByte(),
            charValue = 'A',
            shortValue = 32767.toShort(),
            nullableStringValue = "nullable",
            nullableIntValue = 43,
            nullableLongValue = 9223372036854775806L,
            nullableDoubleValue = 1.618,
            nullableFloatValue = 0.577f,
            nullableBooleanValue = false,
            nullableByteValue = 126.toByte(),
            nullableCharValue = 'B',
            nullableShortValue = 32766.toShort()
        )
        assert(expected == response.body<AllPrimitives>())
    }

    testRoute<AllPrimitives>("Nullable with null values") {
        val response = client.get("/route?stringValue=test&intValue=42&longValue=9223372036854775807&doubleValue=3.14159&floatValue=2.71828&booleanValue=true&byteValue=127&charValue=A&shortValue=32767")
        val expected = AllPrimitives(
            stringValue = "test",
            intValue = 42,
            longValue = 9223372036854775807L,
            doubleValue = 3.14159,
            floatValue = 2.71828f,
            booleanValue = true,
            byteValue = 127.toByte(),
            charValue = 'A',
            shortValue = 32767.toShort(),
            nullableStringValue = null,
            nullableIntValue = null,
            nullableLongValue = null,
            nullableDoubleValue = null,
            nullableFloatValue = null,
            nullableBooleanValue = null,
            nullableByteValue = null,
            nullableCharValue = null,
            nullableShortValue = null
        )
        assert(expected == response.body<AllPrimitives>())
    }

    testRoute<AllPrimitives>("Missing required primitive") {
        val response = client.get("/route?stringValue=test&longValue=9223372036854775807&doubleValue=3.14159&floatValue=2.71828&booleanValue=true&byteValue=127&charValue=A&shortValue=32767")
        assert(HttpStatusCode.BadRequest == response.status)
    }

    testRoute<NullableHeader>("Nullable header with null value") {
        val response = client.get("/route")
        val expected = NullableHeader(nullableHeader = null)
        assert(expected == response.body<NullableHeader>())
    }

    testRoute<NullableBody>("Nullable body with null value") {
        val response = client.get("/route")
        val expected = NullableBody(nullableBody = null)
        assert(expected == response.body<NullableBody>())
    }
}