package com.example

import com.example.codec.Codec
import kotlin.test.Test

class QueryTest {
    @Test
    fun singleQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string)),
            "John"
        )
    }

    @Test
    fun singleNullableQueryWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.stringOrNull)),
            "John"
        )
    }

    @Test
    fun singleNullableQueryWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.stringOrNull)),
            null
        )
    }

    @Test
    fun twoQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int)),
            Params2("John", 32)
        )
    }

    @Test
    fun twoNullableQueryWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.intOrNull)),
            Params2("John", 32)
        )
    }

    @Test
    fun twoNullableQueryWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.intOrNull)),
            Params2("John", null)
        )
    }

    @Test
    fun threeQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean)),
            Params3("John", 32, true)
        )
    }

    @Test
    fun threeQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.booleanOrNull)),
            Params3("John", 32, true)
        )
    }

    @Test
    fun threeQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.booleanOrNull)),
            Params3("John", 32, null)
        )
    }

    @Test
    fun fourQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.float)),
            Params4("John", 32, 123L, 4.5f)
        )
    }

    @Test
    fun fourQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.floatOrNull)),
            Params4("John", 32, 123L, 4.5f)
        )
    }

    @Test
    fun fourQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.floatOrNull)),
            Params4("John", 32, 123L, null)
        )
    }

    @Test
    fun fiveQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte)),
            Params5("John", 32, true, 98.6, 1.toByte())
        )
    }

    @Test
    fun fiveQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byteOrNull)),
            Params5("John", 32, true, 98.6, 1.toByte())
        )
    }

    @Test
    fun fiveQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byteOrNull)),
            Params5("John", 32, true, 98.6, null)
        )
    }

    @Test
    fun sixQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short)),
            Params6("John", 32, true, 98.6, 1.toByte(), 5.toShort())
        )
    }

    @Test
    fun sixQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.shortOrNull)),
            Params6("John", 32, true, 98.6, 1.toByte(), 5.toShort())
        )
    }

    @Test
    fun sixQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.shortOrNull)),
            Params6("John", 32, true, 98.6, 1.toByte(), null)
        )
    }

    @Test
    fun sevenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.long)),
            Params7("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L)
        )
    }

    @Test
    fun sevenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.longOrNull)),
            Params7("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L)
        )
    }

    @Test
    fun sevenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.longOrNull)),
            Params7("John", 32, true, 98.6, 1.toByte(), 5.toShort(), null)
        )
    }

    @Test
    fun eightQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.float)),
            Params8("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f)
        )
    }

    @Test
    fun eightQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.floatOrNull)),
            Params8("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f)
        )
    }

    @Test
    fun eightQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.floatOrNull)),
            Params8("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, null)
        )
    }

    @Test
    fun nineQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.float))
                .query(Parameter.Query("group", Codec.string)),
            Params9("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin")
        )
    }

    @Test
    fun nineQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.float))
                .query(Parameter.Query("group", Codec.stringOrNull)),
            Params9("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin")
        )
    }

    @Test
    fun nineQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.float))
                .query(Parameter.Query("group", Codec.stringOrNull)),
            Params9("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, null)
        )
    }

    @Test
    fun tenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.float))
                .query(Parameter.Query("group", Codec.string))
                .query(Parameter.Query("count", Codec.int)),
            Params10("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin", 42)
        )
    }

    @Test
    fun tenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.float))
                .query(Parameter.Query("group", Codec.string))
                .query(Parameter.Query("count", Codec.intOrNull)),
            Params10("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin", 42)
        )
    }

    @Test
    fun tenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("name", Codec.string))
                .query(Parameter.Query("age", Codec.int))
                .query(Parameter.Query("active", Codec.boolean))
                .query(Parameter.Query("score", Codec.double))
                .query(Parameter.Query("level", Codec.byte))
                .query(Parameter.Query("rank", Codec.short))
                .query(Parameter.Query("id", Codec.long))
                .query(Parameter.Query("rating", Codec.float))
                .query(Parameter.Query("group", Codec.string))
                .query(Parameter.Query("count", Codec.intOrNull)),
            Params10("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin", null)
        )
    }

    @Test
    fun elevenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long)),
            Params11("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L)
        )
    }

    @Test
    fun elevenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.longOrNull)),
            Params11("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L)
        )
    }

    @Test
    fun elevenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.longOrNull)),
            Params11("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, null)
        )
    }

    @Test
    fun twelveQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float)),
            Params12("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f)
        )
    }

    @Test
    fun twelveQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.floatOrNull)),
            Params12("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f)
        )
    }

    @Test
    fun twelveQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.floatOrNull)),
            Params12("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, null)
        )
    }

    @Test
    fun thirteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double)),
            Params13("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0)
        )
    }

    @Test
    fun thirteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.doubleOrNull)),
            Params13("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0)
        )
    }

    @Test
    fun thirteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.doubleOrNull)),
            Params13("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, null)
        )
    }

    @Test
    fun fourteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean)),
            Params14("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false)
        )
    }

    @Test
    fun fourteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.booleanOrNull)),
            Params14("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false)
        )
    }

    @Test
    fun fourteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.booleanOrNull)),
            Params14("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, null)
        )
    }

    @Test
    fun fifteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte)),
            Params15("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte())
        )
    }

    @Test
    fun fifteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byteOrNull)),
            Params15("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte())
        )
    }

    @Test
    fun fifteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byteOrNull)),
            Params15("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, null)
        )
    }

    @Test
    fun sixteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short)),
            Params16("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort())
        )
    }

    @Test
    fun sixteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.shortOrNull)),
            Params16("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort())
        )
    }

    @Test
    fun sixteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.shortOrNull)),
            Params16("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), null)
        )
    }

    @Test
    fun seventeenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string)),
            Params17("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16")
        )
    }

    @Test
    fun seventeenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.stringOrNull)),
            Params17("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16")
        )
    }

    @Test
    fun seventeenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.stringOrNull)),
            Params17("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), null)
        )
    }

    @Test
    fun eighteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int)),
            Params18("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17)
        )
    }

    @Test
    fun eighteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.intOrNull)),
            Params18("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17)
        )
    }

    @Test
    fun eighteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.intOrNull)),
            Params18("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", null)
        )
    }

    @Test
    fun nineteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.long)),
            Params19("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L)
        )
    }

    @Test
    fun nineteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.longOrNull)),
            Params19("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L)
        )
    }

    @Test
    fun nineteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.longOrNull)),
            Params19("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, null)
        )
    }

    @Test
    fun twentyQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.long))
                .query(Parameter.Query("p20", Codec.float)),
            Params20("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, 19f)
        )
    }

    @Test
    fun twentyQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.long))
                .query(Parameter.Query("p20", Codec.floatOrNull)),
            Params20("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, 19f)
        )
    }

    @Test
    fun twentyQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.long))
                .query(Parameter.Query("p20", Codec.floatOrNull)),
            Params20("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, null)
        )
    }

    @Test
    fun twentyOneQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.long))
                .query(Parameter.Query("p20", Codec.float))
                .query(Parameter.Query("p21", Codec.double)),
            Params21("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, 19f, 20.0)
        )
    }

    @Test
    fun twentyOneQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.long))
                .query(Parameter.Query("p20", Codec.float))
                .query(Parameter.Query("p21", Codec.doubleOrNull)),
            Params21("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, 19f, 20.0)
        )
    }

    @Test
    fun twentyOneQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.long))
                .query(Parameter.Query("p20", Codec.float))
                .query(Parameter.Query("p21", Codec.doubleOrNull)),
            Params21("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, 19f, null)
        )
    }

    @Test
    fun twentyTwoQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.long))
                .query(Parameter.Query("p20", Codec.float))
                .query(Parameter.Query("p21", Codec.double))
                .query(Parameter.Query("p22", Codec.boolean)),
            Params22(
                "v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10,
                11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17,
                18L, 19f, 20.0, true
            )
        )
    }

    @Test
    fun twentyTwoQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.long))
                .query(Parameter.Query("p20", Codec.float))
                .query(Parameter.Query("p21", Codec.double))
                .query(Parameter.Query("p22", Codec.booleanOrNull)),
            Params22(
                "v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10,
                11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17,
                18L, 19f, 20.0, true
            )
        )
    }

    @Test
    fun twentyTwoQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query("p1", Codec.string))
                .query(Parameter.Query("p2", Codec.int))
                .query(Parameter.Query("p3", Codec.boolean))
                .query(Parameter.Query("p4", Codec.double))
                .query(Parameter.Query("p5", Codec.byte))
                .query(Parameter.Query("p6", Codec.short))
                .query(Parameter.Query("p7", Codec.long))
                .query(Parameter.Query("p8", Codec.float))
                .query(Parameter.Query("p9", Codec.string))
                .query(Parameter.Query("p10", Codec.int))
                .query(Parameter.Query("p11", Codec.long))
                .query(Parameter.Query("p12", Codec.float))
                .query(Parameter.Query("p13", Codec.double))
                .query(Parameter.Query("p14", Codec.boolean))
                .query(Parameter.Query("p15", Codec.byte))
                .query(Parameter.Query("p16", Codec.short))
                .query(Parameter.Query("p17", Codec.string))
                .query(Parameter.Query("p18", Codec.int))
                .query(Parameter.Query("p19", Codec.long))
                .query(Parameter.Query("p20", Codec.float))
                .query(Parameter.Query("p21", Codec.double))
                .query(Parameter.Query("p22", Codec.booleanOrNull)),
            Params22(
                "v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10,
                11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17,
                18L, 19f, 20.0, null
            )
        )
    }
}
