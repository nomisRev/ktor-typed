package com.example

import com.example.Parameter.Query
import kotlin.test.Test

class QueryTest {
    @Test
    fun singleQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name")),
            "John"
        )
    }

    @Test
    fun singleNullableQueryWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.stringOrNull("name")),
            "John"
        )
    }

    @Test
    fun singleNullableQueryWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.stringOrNull("name")),
            null
        )
    }

    @Test
    fun twoQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age")),
            Params2("John", 32)
        )
    }

    @Test
    fun twoNullableQueryWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.intOrNull("age")),
            Params2("John", 32)
        )
    }

    @Test
    fun twoNullableQueryWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.intOrNull("age")),
            Params2("John", null)
        )
    }

    @Test
    fun threeQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active")),
            Params3("John", 32, true)
        )
    }

    @Test
    fun threeQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.booleanOrNull("active")),
            Params3("John", 32, true)
        )
    }

    @Test
    fun threeQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.booleanOrNull("active")),
            Params3("John", 32, null)
        )
    }

    @Test
    fun fourQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.float("rating")),
            Params4("John", 32, 123L, 4.5f)
        )
    }

    @Test
    fun fourQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.floatOrNull("rating")),
            Params4("John", 32, 123L, 4.5f)
        )
    }

    @Test
    fun fourQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.floatOrNull("rating")),
            Params4("John", 32, 123L, null)
        )
    }

    @Test
    fun fiveQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level")),
            Params5("John", 32, true, 98.6, 1.toByte())
        )
    }

    @Test
    fun fiveQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byteOrNull("level")),
            Params5("John", 32, true, 98.6, 1.toByte())
        )
    }

    @Test
    fun fiveQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byteOrNull("level")),
            Params5("John", 32, true, 98.6, null)
        )
    }

    @Test
    fun sixQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank")),
            Params6("John", 32, true, 98.6, 1.toByte(), 5.toShort())
        )
    }

    @Test
    fun sixQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.shortOrNull("rank")),
            Params6("John", 32, true, 98.6, 1.toByte(), 5.toShort())
        )
    }

    @Test
    fun sixQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.shortOrNull("rank")),
            Params6("John", 32, true, 98.6, 1.toByte(), null)
        )
    }

    @Test
    fun sevenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.long("id")),
            Params7("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L)
        )
    }

    @Test
    fun sevenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.longOrNull("id")),
            Params7("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L)
        )
    }

    @Test
    fun sevenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(com.example.Query.longOrNull("id")),
            Params7("John", 32, true, 98.6, 1.toByte(), 5.toShort(), null)
        )
    }

    @Test
    fun eightQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.float("rating")),
            Params8("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f)
        )
    }

    @Test
    fun eightQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.floatOrNull("rating")),
            Params8("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f)
        )
    }

    @Test
    fun eightQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.floatOrNull("rating")),
            Params8("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, null)
        )
    }

    @Test
    fun nineQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.float("rating"))
                .query(Parameter.Query.string("group")),
            Params9("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin")
        )
    }

    @Test
    fun nineQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.float("rating"))
                .query(Parameter.Query.stringOrNull("group")),
            Params9("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin")
        )
    }


    @Test
    fun nineQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.float("rating"))
                .query(Parameter.Query.stringOrNull("group")),
            Params9("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, null)
        )
    }

    @Test
    fun tenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.float("rating"))
                .query(Parameter.Query.string("group"))
                .query(Parameter.Query.int("count")),
            Params10("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin", 42)
        )
    }

    @Test
    fun tenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.float("rating"))
                .query(Parameter.Query.string("group"))
                .query(Parameter.Query.intOrNull("count")),
            Params10("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin", 42)
        )
    }

    @Test
    fun tenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("name"))
                .query(Parameter.Query.int("age"))
                .query(Parameter.Query.boolean("active"))
                .query(Parameter.Query.double("score"))
                .query(Parameter.Query.byte("level"))
                .query(Parameter.Query.short("rank"))
                .query(Parameter.Query.long("id"))
                .query(Parameter.Query.float("rating"))
                .query(Parameter.Query.string("group"))
                .query(Parameter.Query.intOrNull("count")),
            Params10("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin", null)
        )
    }

    @Test
    fun elevenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11")),
            Params11("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L)
        )
    }

    @Test
    fun elevenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.longOrNull("p11")),
            Params11("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L)
        )
    }

    @Test
    fun elevenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.longOrNull("p11")),
            Params11("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, null)
        )
    }

    @Test
    fun twelveQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12")),
            Params12("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f)
        )
    }

    @Test
    fun twelveQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.floatOrNull("p12")),
            Params12("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f)
        )
    }

    @Test
    fun twelveQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.floatOrNull("p12")),
            Params12("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, null)
        )
    }

    @Test
    fun thirteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13")),
            Params13("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0)
        )
    }

    @Test
    fun thirteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.doubleOrNull("p13")),
            Params13("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0)
        )
    }

    @Test
    fun thirteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.doubleOrNull("p13")),
            Params13("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, null)
        )
    }

    @Test
    fun fourteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14")),
            Params14("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false)
        )
    }

    @Test
    fun fourteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.booleanOrNull("p14")),
            Params14("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false)
        )
    }

    @Test
    fun fourteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.booleanOrNull("p14")),
            Params14("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, null)
        )
    }

    @Test
    fun fifteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15")),
            Params15("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte())
        )
    }

    @Test
    fun fifteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byteOrNull("p15")),
            Params15("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte())
        )
    }

    @Test
    fun fifteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byteOrNull("p15")),
            Params15("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, null)
        )
    }

    @Test
    fun sixteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16")),
            Params16(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort()
            )
        )
    }

    @Test
    fun sixteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.shortOrNull("p16")),
            Params16(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort()
            )
        )
    }

    @Test
    fun sixteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.shortOrNull("p16")),
            Params16(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                null
            )
        )
    }

    @Test
    fun seventeenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17")),
            Params17(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16"
            )
        )
    }

    @Test
    fun seventeenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.stringOrNull("p17")),
            Params17(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16"
            )
        )
    }

    @Test
    fun seventeenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.stringOrNull("p17")),
            Params17(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                null
            )
        )
    }

    @Test
    fun eighteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18")),
            Params18(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17
            )
        )
    }

    @Test
    fun eighteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.intOrNull("p18")),
            Params18(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17
            )
        )
    }

    @Test
    fun eighteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.intOrNull("p18")),
            Params18(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                null
            )
        )
    }

    @Test
    fun nineteenQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.long("p19")),
            Params19(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17,
                18L
            )
        )
    }

    @Test
    fun nineteenQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.longOrNull("p19")),
            Params19(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17,
                18L
            )
        )
    }

    @Test
    fun nineteenQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.longOrNull("p19")),
            Params19(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17,
                null
            )
        )
    }

    @Test
    fun twentyQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.long("p19"))
                .query(Parameter.Query.float("p20")),
            Params20(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17,
                18L,
                19f
            )
        )
    }

    @Test
    fun twentyQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.long("p19"))
                .query(Parameter.Query.floatOrNull("p20")),
            Params20(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17,
                18L,
                19f
            )
        )
    }

    @Test
    fun twentyQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.long("p19"))
                .query(Parameter.Query.floatOrNull("p20")),
            Params20(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17,
                18L,
                null
            )
        )
    }

    @Test
    fun twentyOneQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.long("p19"))
                .query(Parameter.Query.float("p20"))
                .query(Parameter.Query.double("p21")),
            Params21(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17,
                18L,
                19f,
                20.0
            )
        )
    }

    @Test
    fun twentyOneQueryParametersWithNullableWithValue() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.long("p19"))
                .query(Parameter.Query.float("p20"))
                .query(Parameter.Query.doubleOrNull("p21")),
            Params21(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17,
                18L,
                19f,
                20.0
            )
        )
    }

    @Test
    fun twentyOneQueryParametersWithNullableWithNull() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.long("p19"))
                .query(Parameter.Query.float("p20"))
                .query(Parameter.Query.doubleOrNull("p21")),
            Params21(
                "v1",
                2,
                true,
                4.0,
                5.toByte(),
                6.toShort(),
                7L,
                8f,
                "v9",
                10,
                11L,
                12f,
                13.0,
                false,
                14.toByte(),
                15.toShort(),
                "v16",
                17,
                18L,
                19f,
                null
            )
        )
    }

    @Test
    fun twentyTwoQueryParameters() {
        testInput(
            pathOf("simple")
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.long("p19"))
                .query(Parameter.Query.float("p20"))
                .query(Parameter.Query.double("p21"))
                .query(Parameter.Query.boolean("p22")),
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
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.long("p19"))
                .query(Parameter.Query.float("p20"))
                .query(Parameter.Query.double("p21"))
                .query(Parameter.Query.booleanOrNull("p22")),
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
                .query(Parameter.Query.string("p1"))
                .query(Parameter.Query.int("p2"))
                .query(Parameter.Query.boolean("p3"))
                .query(Parameter.Query.double("p4"))
                .query(Parameter.Query.byte("p5"))
                .query(Parameter.Query.short("p6"))
                .query(Parameter.Query.long("p7"))
                .query(Parameter.Query.float("p8"))
                .query(Parameter.Query.string("p9"))
                .query(Parameter.Query.int("p10"))
                .query(Parameter.Query.long("p11"))
                .query(Parameter.Query.float("p12"))
                .query(Parameter.Query.double("p13"))
                .query(Parameter.Query.boolean("p14"))
                .query(Parameter.Query.byte("p15"))
                .query(Parameter.Query.short("p16"))
                .query(Parameter.Query.string("p17"))
                .query(Parameter.Query.int("p18"))
                .query(Parameter.Query.long("p19"))
                .query(Parameter.Query.float("p20"))
                .query(Parameter.Query.double("p21"))
                .query(Parameter.Query.booleanOrNull("p22")),
            Params22(
                "v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10,
                11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17,
                18L, 19f, 20.0, null
            )
        )
    }
}
