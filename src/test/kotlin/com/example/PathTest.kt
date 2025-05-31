package com.example

import kotlin.test.Test
import kotlin.test.assertEquals


class PathTest {
    @Test
    fun nonePathParameters() {
        testInput(pathOf("simple"), Unit)
    }

    @Test
    fun singlePathParameters() {
        testInput(
            Path { "users" / string("name") },
            "John"
        )
    }

    @Test
    fun singleNullablePathWithValue() {
        testInput(
            Path { "users" / stringOrNull("name") },
            "John"
        )
    }

    @Test
    fun singleNullablePathWithNull() {
        testInput(
            Path { "users" / stringOrNull("name") },
            null
        )
    }

    @Test
    fun twoPathParameters() {
        testInput(
            Path { "users" / string("name") / int("age") },
            Params2("John", 32)
        )
    }

    @Test
    fun twoNullablePathWithValue() {
        testInput(
            Path { "users" / string("name") / intOrNull("age") },
            Params2("John", 32)
        )
    }

    @Test
    fun twoNullablePathWithNull() {
        testInput(
            Path { "users" / string("name") / intOrNull("age") },
            Params2("John", null)
        )
    }

    @Test
    fun threePathParameters() {
        testInput(
            Path { "users" / string("name") / int("age") / bool("active") },
            Params3("John", 32, true)
        )
    }

    @Test
    fun threePathParametersWithNullableWithValue() {
        testInput(
            Path { "users" / string("name") / int("age") / boolOrNull("active") },
            Params3("John", 32, true)
        )
    }

    @Test
    fun threePathParametersWithNullableWithNull() {
        testInput(
            Path { "users" / string("name") / int("age") / boolOrNull("active") },
            Params3("John", 32, null)
        )
    }

    @Test
    fun fourPathParameters() {
        testInput(
            Path { "users" / string("name") / int("age") / long("id") / float("rating") },
            Params4("John", 32, 123L, 4.5f)
        )
    }

    @Test
    fun fourPathParametersWithNullableWithValue() {
        testInput(
            Path { "users" / string("name") / int("age") / long("id") / floatOrNull("rating") },
            Params4("John", 32, 123L, 4.5f)
        )
    }

    @Test
    fun fourPathParametersWithNullableWithNull() {
        testInput(
            Path { "users" / string("name") / int("age") / long("id") / floatOrNull("rating") },
            Params4("John", 32, 123L, null)
        )
    }

    @Test
    fun fivePathParameters() {
        testInput(
            Path { "users" / string("name") / int("age") / bool("active") / double("score") / byte("level") },
            Params5("John", 32, true, 98.6, 1.toByte())
        )
    }

    @Test
    fun fivePathParametersWithNullableWithValue() {
        testInput(
            Path { "users" / string("name") / int("age") / bool("active") / double("score") / byteOrNull("level") },
            Params5("John", 32, true, 98.6, 1.toByte())
        )
    }

    @Test
    fun fivePathParametersWithNullableWithNull() {
        testInput(
            Path { "users" / string("name") / int("age") / bool("active") / double("score") / byteOrNull("level") },
            Params5("John", 32, true, 98.6, null)
        )
    }

    @Test
    fun sixPathParameters() {
        testInput(
            Path { "users" / string("name") / int("age") / bool("active") / double("score") / byte("level") / short("rank") },
            Params6("John", 32, true, 98.6, 1.toByte(), 5.toShort())
        )
    }

    @Test
    fun sixPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") / byte("level") / shortOrNull(
                    "rank"
                )
            },
            Params6("John", 32, true, 98.6, 1.toByte(), 5.toShort())
        )
    }

    @Test
    fun sixPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") / byte("level") / shortOrNull(
                    "rank"
                )
            },
            Params6("John", 32, true, 98.6, 1.toByte(), null)
        )
    }

    @Test
    fun otherTypesWithNullable() {
        // Test for double nullable
        testInput(
            Path { "metrics" / string("name") / doubleOrNull("value") },
            Params2("temperature", 36.6)
        )

        testInput(
            Path { "metrics" / string("name") / doubleOrNull("value") },
            Params2("temperature", null)
        )

        // Test for long nullable
        testInput(
            Path { "users" / string("name") / longOrNull("id") },
            Params2("John", 12345678901234L)
        )

        testInput(
            Path { "users" / string("name") / longOrNull("id") },
            Params2("John", null)
        )

        // Test for short nullable
        testInput(
            Path { "items" / string("name") / shortOrNull("quantity") },
            Params2("apple", 10.toShort())
        )

        testInput(
            Path { "items" / string("name") / shortOrNull("quantity") },
            Params2("apple", null)
        )
    }

    @Test
    fun staticSegmentAfterParameter() {
        testInput(
            Path { "users" / string("name") / "profile" },
            "John"
        )
    }

    @Test
    fun testRouteString() {
        val path1 = Path { "users" / string("name") }
        assertEquals("users/{name}", path1.path.routeString())

        val path2 = Path { "users" / stringOrNull("name") }
        assertEquals("users/{name?}", path2.path.routeString())

        val path3 = Path { "users" / string("name") / "profile" }
        assertEquals("users/{name}/profile", path3.path.routeString())

        val path4 = Path { "users" / string("name") / int("age") / bool("active") }
        assertEquals("users/{name}/{age}/{active}", path4.path.routeString())
    }

    @Test
    fun sevenPathParameters() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / long("id")
            },
            Params7("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L)
        )
    }

    @Test
    fun sevenPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / longOrNull("id")
            },
            Params7("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L)
        )
    }

    @Test
    fun sevenPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / longOrNull("id")
            },
            Params7("John", 32, true, 98.6, 1.toByte(), 5.toShort(), null)
        )
    }

    @Test
    fun eightPathParameters() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / long("id") / float("rating")
            },
            Params8("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f)
        )
    }

    @Test
    fun eightPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / long("id") / floatOrNull("rating")
            },
            Params8("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f)
        )
    }

    @Test
    fun eightPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / long("id") / floatOrNull("rating")
            },
            Params8("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, null)
        )
    }

    @Test
    fun ninePathParameters() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / long("id") / float("rating") / string("group")
            },
            Params9("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin")
        )
    }

    @Test
    fun ninePathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / long("id") / float("rating") / stringOrNull("group")
            },
            Params9("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin")
        )
    }

    @Test
    fun ninePathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / long("id") / float("rating") / stringOrNull("group")
            },
            Params9("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, null)
        )
    }

    @Test
    fun tenPathParameters() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / long("id") / float("rating") / string("group") / int("count")
            },
            Params10("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin", 42)
        )
    }

    @Test
    fun tenPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / long("id") / float("rating") / string("group") / intOrNull("count")
            },
            Params10("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin", 42)
        )
    }

    @Test
    fun tenPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("name") / int("age") / bool("active") / double("score") /
                        byte("level") / short("rank") / long("id") / float("rating") / string("group") / intOrNull("count")
            },
            Params10("John", 32, true, 98.6, 1.toByte(), 5.toShort(), 123456789L, 4.5f, "admin", null)
        )
    }

    @Test
    fun elevenPathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11")
            },
            Params11("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L)
        )
    }

    @Test
    fun elevenPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / longOrNull("p11")
            },
            Params11("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L)
        )
    }

    @Test
    fun elevenPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / longOrNull("p11")
            },
            Params11("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, null)
        )
    }

    @Test
    fun twelvePathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12")
            },
            Params12("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f)
        )
    }

    @Test
    fun twelvePathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / floatOrNull("p12")
            },
            Params12("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f)
        )
    }

    @Test
    fun twelvePathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / floatOrNull("p12")
            },
            Params12("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, null)
        )
    }

    @Test
    fun thirteenPathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13")
            },
            Params13("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0)
        )
    }

    @Test
    fun thirteenPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / doubleOrNull("p13")
            },
            Params13("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0)
        )
    }

    @Test
    fun thirteenPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / doubleOrNull("p13")
            },
            Params13("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, null)
        )
    }

    @Test
    fun fourteenPathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14")
            },
            Params14("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false)
        )
    }

    @Test
    fun fourteenPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / boolOrNull("p14")
            },
            Params14("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false)
        )
    }

    @Test
    fun fourteenPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / boolOrNull("p14")
            },
            Params14("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, null)
        )
    }

    @Test
    fun fifteenPathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") / byte("p15")
            },
            Params15("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte())
        )
    }

    @Test
    fun fifteenPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") / byteOrNull("p15")
            },
            Params15("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte())
        )
    }

    @Test
    fun fifteenPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") / byteOrNull("p15")
            },
            Params15("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, null)
        )
    }

    @Test
    fun sixteenPathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") / byte("p15") / short("p16")
            },
            Params16("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort())
        )
    }

    @Test
    fun sixteenPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") / byte("p15") / shortOrNull("p16")
            },
            Params16("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort())
        )
    }

    @Test
    fun sixteenPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") / byte("p15") / shortOrNull("p16")
            },
            Params16("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), null)
        )
    }

    @Test
    fun seventeenPathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17")
            },
            Params17("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16")
        )
    }

    @Test
    fun seventeenPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / stringOrNull("p17")
            },
            Params17("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16")
        )
    }

    @Test
    fun seventeenPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / stringOrNull("p17")
            },
            Params17("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), null)
        )
    }

    @Test
    fun eighteenPathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / int("p18")
            },
            Params18("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17)
        )
    }

    @Test
    fun eighteenPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / intOrNull("p18")
            },
            Params18("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17)
        )
    }

    @Test
    fun eighteenPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / intOrNull("p18")
            },
            Params18("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", null)
        )
    }

    @Test
    fun nineteenPathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / int("p18") / long("p19")
            },
            Params19("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L)
        )
    }

    @Test
    fun nineteenPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / int("p18") / longOrNull("p19")
            },
            Params19("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L)
        )
    }

    @Test
    fun nineteenPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / int("p18") / longOrNull("p19")
            },
            Params19("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, null)
        )
    }

    @Test
    fun twentyPathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / int("p18") / long("p19") / float("p20")
            },
            Params20("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, 19f)
        )
    }

    @Test
    fun twentyPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / int("p18") / long("p19") / floatOrNull("p20")
            },
            Params20("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, 19f)
        )
    }

    @Test
    fun twentyPathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / int("p18") / long("p19") / floatOrNull("p20")
            },
            Params20("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, null)
        )
    }

    @Test
    fun twentyOnePathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / int("p18") / long("p19") /
                        float("p20") / double("p21")
            },
            Params21("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, 19f, 20.0)
        )
    }

    @Test
    fun twentyOnePathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / int("p18") / long("p19") /
                        float("p20") / doubleOrNull("p21")
            },
            Params21("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, 19f, 20.0)
        )
    }

    @Test
    fun twentyOnePathParametersWithNullableWithNull() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / bool("p3") / double("p4") /
                        byte("p5") / short("p6") / long("p7") / float("p8") / string("p9") /
                        int("p10") / long("p11") / float("p12") / double("p13") / bool("p14") /
                        byte("p15") / short("p16") / string("p17") / int("p18") / long("p19") /
                        float("p20") / doubleOrNull("p21")
            },
            Params21("v1", 2, true, 4.0, 5.toByte(), 6.toShort(), 7L, 8f, "v9", 10, 11L, 12f, 13.0, false, 14.toByte(), 15.toShort(), "v16", 17, 18L, 19f, null)
        )
    }

    @Test
    fun twentyTwoPathParameters() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / long("p3") / float("p4") / double("p5") /
                        bool("p6") / byte("p7") / short("p8") / string("p9") / int("p10") /
                        long("p11") / float("p12") / double("p13") / bool("p14") / byte("p15") /
                        short("p16") / string("p17") / int("p18") / long("p19") / float("p20") /
                        double("p21") / bool("p22")
            },
            Params22(
                "v1", 2, 3L, 4f, 5.0, true, 6.toByte(), 7.toShort(), "v8", 9,
                10L, 11f, 12.0, false, 13.toByte(), 14.toShort(), "v15", 16,
                17L, 18f, 19.0, true
            )
        )
    }

    @Test
    fun twentyTwoPathParametersWithNullableWithValue() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / long("p3") / float("p4") / double("p5") /
                        bool("p6") / byte("p7") / short("p8") / string("p9") / int("p10") /
                        long("p11") / float("p12") / double("p13") / bool("p14") / byte("p15") /
                        short("p16") / string("p17") / int("p18") / long("p19") / float("p20") /
                        double("p21") / boolOrNull("p22")
            },
            Params22(
                "v1", 2, 3L, 4f, 5.0, true, 6.toByte(), 7.toShort(), "v8", 9,
                10L, 11f, 12.0, false, 13.toByte(), 14.toShort(), "v15", 16,
                17L, 18f, 19.0, true
            )
        )
    }

    @Test
    fun twentyTwoPathParametersWithNullableAtEnd() {
        testInput(
            Path {
                "users" / string("p1") / int("p2") / long("p3") / float("p4") / double("p5") /
                        bool("p6") / byte("p7") / short("p8") / string("p9") / int("p10") /
                        long("p11") / float("p12") / double("p13") / bool("p14") / byte("p15") /
                        short("p16") / string("p17") / int("p18") / long("p19") / float("p20") /
                        double("p21") / boolOrNull("p22")
            },
            Params22(
                "v1", 2, 3L, 4f, 5.0, true, 6.toByte(), 7.toShort(), "v8", 9,
                10L, 11f, 12.0, false, 13.toByte(), 14.toShort(), "v15", 16,
                17L, 18f, 19.0, null
            )
        )
    }
}
