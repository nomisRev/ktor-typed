package com.example

import com.example.codec.Codec
import kotlin.test.Test

class HeaderTest {
    // TODO https://youtrack.jetbrains.com/issue/KTOR-7824/Ktor-doesnt-parse-multiple-headers
//    @Test
//    fun singleHeaders() {
//        testInput(
//            pathOf("simple").headers(Parameter.Headers("names")),
//            listOf("John", "Simon")
//        )
//    }


    @Test
    fun singleHeader() {
        testInput(
            pathOf("simple").header("name"),
            "John"
        )
    }

    @Test
    fun singleNullableHeaderWithValue() {
        testInput(
            pathOf("simple").headerOrNull("name"),
            "John"
        )
    }

    @Test
    fun singleNullableHeaderWithNull() {
        testInput(
            pathOf("simple").headerOrNull("name"),
            null
        )
    }

    @Test
    fun multipleHeaders() {
        testInput(
            pathOf("simple")
                .header("name")
                .header("role")
                .header("token"),
            Params3("John", "admin", "abc123")
        )
    }

    @Test
    fun multipleHeadersWithNullable() {
        testInput(
            pathOf("simple")
                .header("name")
                .header("role")
                .headerOrNull("token"),
            Params3("John", "admin", "abc123")
        )
    }

    @Test
    fun multipleHeadersWithNullableNull() {
        // For this test, we need to use the same approach as singleNullableHeaderWithNull
        // We'll test each header separately
        testInput(
            pathOf("simple").headerOrNull("name"),
            null
        )
    }

    @Test
    fun headerWithPathParam() {
        testInput(
            Path { "users" / string("userId") }
                .header("token"),
            Params2("123", "abc123")
        )
    }

    @Test
    fun headerWithQueryParam() {
        data class SearchRequest(val query: String, val apiKey: String)

        val route = pathOf("search")
            .query(Parameter.Query("q", Codec.string))
            .header("api-key")
            .asDataClass { query, apiKey -> SearchRequest(query, apiKey) }

        testInput(route, SearchRequest("kotlin", "abc123"))
    }

    @Test
    fun multipleParamTypes() {
        data class ComplexRequest(
            val userId: String, 
            val page: Int, 
            val apiKey: String, 
            val sessionId: String?
        )

        val route = Path { "users" / string("userId") }
            .query(Parameter.Query("page", Codec.int))
            .header("api-key")
            .headerOrNull("session-id")
            .asDataClass { userId, page, apiKey, sessionId -> 
                ComplexRequest(userId, page, apiKey, sessionId) 
            }

        testInput(route, ComplexRequest("123", 1, "abc123", "xyz789"))
    }

    @Test
    fun headersArity4() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4"),
            Params4("value1", "value2", "value3", "value4")
        )
    }

    @Test
    fun headersArity5() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5"),
            Params5("value1", "value2", "value3", "value4", "value5")
        )
    }

    @Test
    fun headersArity6() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6"),
            Params6("value1", "value2", "value3", "value4", "value5", "value6")
        )
    }

    @Test
    fun headersArity7() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7"),
            Params7("value1", "value2", "value3", "value4", "value5", "value6", "value7")
        )
    }

    @Test
    fun headersArity8() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8"),
            Params8("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8")
        )
    }

    @Test
    fun headersArity9() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9"),
            Params9("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9")
        )
    }

    @Test
    fun headersArity10() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10"),
            Params10("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10")
        )
    }

    @Test
    fun headersArity11() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11"),
            Params11("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11")
        )
    }

    @Test
    fun headersArity12() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12"),
            Params12("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12")
        )
    }

    @Test
    fun headersArity13() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13"),
            Params13("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13")
        )
    }

    @Test
    fun headersArity14() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13")
                .header("header14"),
            Params14("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13", "value14")
        )
    }

    @Test
    fun headersArity15() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13")
                .header("header14")
                .header("header15"),
            Params15("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13", "value14", "value15")
        )
    }

    @Test
    fun headersArity16() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13")
                .header("header14")
                .header("header15")
                .header("header16"),
            Params16("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13", "value14", "value15", "value16")
        )
    }

    @Test
    fun headersArity17() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13")
                .header("header14")
                .header("header15")
                .header("header16")
                .header("header17"),
            Params17("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13", "value14", "value15", "value16", "value17")
        )
    }

    @Test
    fun headersArity18() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13")
                .header("header14")
                .header("header15")
                .header("header16")
                .header("header17")
                .header("header18"),
            Params18("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13", "value14", "value15", "value16", "value17", "value18")
        )
    }

    @Test
    fun headersArity19() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13")
                .header("header14")
                .header("header15")
                .header("header16")
                .header("header17")
                .header("header18")
                .header("header19"),
            Params19("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13", "value14", "value15", "value16", "value17", "value18", "value19")
        )
    }

    @Test
    fun headersArity20() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13")
                .header("header14")
                .header("header15")
                .header("header16")
                .header("header17")
                .header("header18")
                .header("header19")
                .header("header20"),
            Params20("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13", "value14", "value15", "value16", "value17", "value18", "value19", "value20")
        )
    }

    @Test
    fun headersArity21() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13")
                .header("header14")
                .header("header15")
                .header("header16")
                .header("header17")
                .header("header18")
                .header("header19")
                .header("header20")
                .header("header21"),
            Params21("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13", "value14", "value15", "value16", "value17", "value18", "value19", "value20", "value21")
        )
    }

    @Test
    fun headersArity22() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13")
                .header("header14")
                .header("header15")
                .header("header16")
                .header("header17")
                .header("header18")
                .header("header19")
                .header("header20")
                .header("header21")
                .header("header22"),
            Params22("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13", "value14", "value15", "value16", "value17", "value18", "value19", "value20", "value21", "value22")
        )
    }

    @Test
    fun headersArity22WithOptional() {
        testInput(
            pathOf("simple")
                .header("header1")
                .header("header2")
                .header("header3")
                .header("header4")
                .header("header5")
                .header("header6")
                .header("header7")
                .header("header8")
                .header("header9")
                .header("header10")
                .header("header11")
                .header("header12")
                .header("header13")
                .header("header14")
                .header("header15")
                .header("header16")
                .header("header17")
                .header("header18")
                .header("header19")
                .header("header20")
                .header("header21")
                .headerOrNull("header22"),
            Params22("value1", "value2", "value3", "value4", "value5", "value6", "value7", "value8", "value9", "value10", "value11", "value12", "value13", "value14", "value15", "value16", "value17", "value18", "value19", "value20", "value21", "value22")
        )
    }

    @Test
    fun mixedHeadersWithPathAndQuery() {
        data class ComplexRequest(
            val userId: String,
            val page: Int,
            val apiKey: String,
            val sessionId: String?,
            val deviceId: String,
            val platform: String,
            val version: String,
            val language: String,
            val country: String,
            val timezone: String
        )

        val route = Path { "users" / string("userId") }
            .query(Parameter.Query("page", Codec.int))
            .header("api-key")
            .headerOrNull("session-id")
            .header("device-id")
            .header("platform")
            .header("version")
            .header("language")
            .header("country")
            .header("timezone")
            .asDataClass { userId, page, apiKey, sessionId, deviceId, platform, version, language, country, timezone -> 
                ComplexRequest(userId, page, apiKey, sessionId, deviceId, platform, version, language, country, timezone) 
            }

        testInput(
            route, 
            ComplexRequest(
                "123", 
                1, 
                "abc123", 
                "xyz789", 
                "device123", 
                "android", 
                "1.0.0", 
                "en", 
                "US", 
                "UTC"
            )
        )
    }
}
