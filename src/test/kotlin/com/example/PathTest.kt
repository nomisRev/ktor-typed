package com.example

import kotlin.test.Test


class PathTest {
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
