//package com.example.test
//
//import io.ktor.client.call.body
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.request.get
//import io.ktor.http.HttpStatusCode
//import io.ktor.route.simple.get
//import io.ktor.serialization.kotlinx.json.json
//import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
//import io.ktor.server.response.respond
//import io.ktor.server.testing.testApplication
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertNull
//
//class PrimitiveTypesTest {
//    @Test
//    fun testAllPrimitives() {
//        testApplication {
//            routing {
//                install(ServerContentNegotiation) { json() }
//                get<AllPrimitives>("/primitives") { value ->
//                    call.respond(HttpStatusCode.OK, value)
//                }
//            }
//
//            val client = createClient {
//                install(ContentNegotiation) { json() }
//            }
//
//            val primitives = client.get("/primitives?stringValue=test&intValue=42&longValue=9223372036854775807&doubleValue=3.14159&floatValue=2.71828&booleanValue=true&byteValue=127&charValue=A&shortValue=32767&nullableStringValue=nullable&nullableIntValue=43&nullableLongValue=9223372036854775806&nullableDoubleValue=1.618&nullableFloatValue=0.577&nullableBooleanValue=false&nullableByteValue=126&nullableCharValue=B&nullableShortValue=32766").body<AllPrimitives>()
//
//            assertEquals("test", primitives.stringValue)
//            assertEquals(42, primitives.intValue)
//            assertEquals(9223372036854775807L, primitives.longValue)
//            assertEquals(3.14159, primitives.doubleValue)
//            assertEquals(2.71828f, primitives.floatValue)
//            assertEquals(true, primitives.booleanValue)
//            assertEquals(127.toByte(), primitives.byteValue)
//            assertEquals('A', primitives.charValue)
//            assertEquals(32767.toShort(), primitives.shortValue)
//            assertEquals("nullable", primitives.nullableStringValue)
//            assertEquals(43, primitives.nullableIntValue)
//            assertEquals(9223372036854775806L, primitives.nullableLongValue)
//            assertEquals(1.618, primitives.nullableDoubleValue)
//            assertEquals(0.577f, primitives.nullableFloatValue)
//            assertEquals(false, primitives.nullableBooleanValue)
//            assertEquals(126.toByte(), primitives.nullableByteValue)
//            assertEquals('B', primitives.nullableCharValue)
//            assertEquals(32766.toShort(), primitives.nullableShortValue)
//        }
//    }
//
//    @Test
//    fun testNullableWithNullValues() {
//        testApplication {
//            routing {
//                install(ServerContentNegotiation) { json() }
//                get<AllPrimitives>("/primitives") { value ->
//                    call.respond(HttpStatusCode.OK, value)
//                }
//            }
//
//            val client = createClient {
//                install(ContentNegotiation) { json() }
//            }
//
//            val primitives = client.get("/primitives?stringValue=test&intValue=42&longValue=9223372036854775807&doubleValue=3.14159&floatValue=2.71828&booleanValue=true&byteValue=127&charValue=A&shortValue=32767").body<AllPrimitives>()
//
//            assertEquals("test", primitives.stringValue)
//            assertEquals(42, primitives.intValue)
//            assertEquals(9223372036854775807L, primitives.longValue)
//            assertEquals(3.14159, primitives.doubleValue)
//            assertEquals(2.71828f, primitives.floatValue)
//            assertEquals(true, primitives.booleanValue)
//            assertEquals(127.toByte(), primitives.byteValue)
//            assertEquals('A', primitives.charValue)
//            assertEquals(32767.toShort(), primitives.shortValue)
//            assertNull(primitives.nullableStringValue)
//            assertNull(primitives.nullableIntValue)
//            assertNull(primitives.nullableLongValue)
//            assertNull(primitives.nullableDoubleValue)
//            assertNull(primitives.nullableFloatValue)
//            assertNull(primitives.nullableBooleanValue)
//            assertNull(primitives.nullableByteValue)
//            assertNull(primitives.nullableCharValue)
//            assertNull(primitives.nullableShortValue)
//        }
//    }
//
//    @Test
//    fun testMissingRequiredPrimitive() {
//        testApplication {
//            routing {
//                install(ServerContentNegotiation) { json() }
//                get<AllPrimitives>("/primitives") { value ->
//                    call.respond(HttpStatusCode.OK, value)
//                }
//            }
//
//            val client = createClient {
//                install(ContentNegotiation) { json() }
//            }
//
//            val response = client.get("/primitives?stringValue=test&longValue=9223372036854775807&doubleValue=3.14159&floatValue=2.71828&booleanValue=true&byteValue=127&charValue=A&shortValue=32767")
//            assertEquals(HttpStatusCode.BadRequest, response.status)
//        }
//    }
//
//    @Test
//    fun testNullableHeaderWithNullValue() {
//        testApplication {
//            routing {
//                install(ServerContentNegotiation) { json() }
//                get<NullableHeader>("/nullable-header") { value ->
//                    call.respond(HttpStatusCode.OK, value)
//                }
//            }
//
//            val client = createClient {
//                install(ContentNegotiation) { json() }
//            }
//
//            val response = client.get("/nullable-header")
//            assertEquals(HttpStatusCode.OK, response.status)
//
//            val nullableHeader = response.body<NullableHeader>()
//            assertNull(nullableHeader.nullableHeader)
//        }
//    }
//
//    @Test
//    fun testNullableBodyWithNullValue() {
//        testApplication {
//            routing {
//                install(ServerContentNegotiation) { json() }
//                get<NullableBody>("/nullable-body") { value ->
//                    call.respond(HttpStatusCode.OK, value)
//                }
//            }
//
//            val client = createClient {
//                install(ContentNegotiation) { json() }
//            }
//
//            val response = client.get("/nullable-body")
//            assertEquals(HttpStatusCode.OK, response.status)
//            assertEquals(response.body<NullableBody>(), NullableBody(null))
//        }
//    }
//}
