//package com.example.test
//
//import io.ktor.client.call.body
//import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
//import io.ktor.client.request.get
//import io.ktor.http.HttpStatusCode
//import io.ktor.route.simple.route
//import io.ktor.serialization.kotlinx.json.json
//import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
//import io.ktor.server.response.respond
//import io.ktor.server.routing.routing
//import io.ktor.server.testing.testApplication
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import com.example.test.ListPrimitives
//
//class ListPrimitivesTest {
//    @Test
//    fun testListPrimitives() {
//        testApplication {
//            routing {
//                install(ServerContentNegotiation) { json() }
//                route<ListPrimitives>("/list-primitives") { value ->
//                    call.respond(HttpStatusCode.OK, value)
//                }
//            }
//
//            val client = createClient {
//                install(ContentNegotiation) { json() }
//            }
//
//            val primitives = client.get("/list-primitives") {
//                url.parameters.appendAll("stringList", listOf("a", "b", "c"))
//                url.parameters.appendAll("intList", listOf("1", "2", "3"))
//                url.parameters.appendAll("longList", listOf("1000000000000", "2000000000000", "3000000000000"))
//                url.parameters.appendAll("doubleList", listOf("1.1", "2.2", "3.3"))
//                url.parameters.appendAll("floatList", listOf("1.1", "2.2", "3.3"))
//                url.parameters.appendAll("booleanList", listOf("true", "false", "true"))
//                url.parameters.appendAll("byteList", listOf("1", "2", "3"))
//                url.parameters.appendAll("charList", listOf("a", "b", "c"))
//                url.parameters.appendAll("shortList", listOf("1", "2", "3"))
//                url.parameters.appendAll("nullableStringList", listOf("a", "b", "c"))
//                url.parameters.appendAll("nullableIntList", listOf("1", "2", "3"))
//                url.parameters.appendAll("nullableLongList", listOf("1000000000000", "2000000000000", "3000000000000"))
//                url.parameters.appendAll("nullableDoubleList", listOf("1.1", "2.2", "3.3"))
//                url.parameters.appendAll("nullableFloatList", listOf("1.1", "2.2", "3.3"))
//                url.parameters.appendAll("nullableBooleanList", listOf("true", "false", "true"))
//                url.parameters.appendAll("nullableByteList", listOf("1", "2", "3"))
//                url.parameters.appendAll("nullableCharList", listOf("a", "b", "c"))
//                url.parameters.appendAll("nullableShortList", listOf("1", "2", "3"))
//            }.body<ListPrimitives>()
//
//            assertEquals(listOf("a", "b", "c"), primitives.stringList)
//            assertEquals(listOf(1, 2, 3), primitives.intList)
//            assertEquals(listOf(1000000000000L, 2000000000000L, 3000000000000L), primitives.longList)
//            assertEquals(listOf(1.1, 2.2, 3.3), primitives.doubleList)
//            assertEquals(listOf(1.1f, 2.2f, 3.3f), primitives.floatList)
//            assertEquals(listOf(true, false, true), primitives.booleanList)
//            assertEquals(listOf(1.toByte(), 2.toByte(), 3.toByte()), primitives.byteList)
//            assertEquals(listOf('a', 'b', 'c'), primitives.charList)
//            assertEquals(listOf(1.toShort(), 2.toShort(), 3.toShort()), primitives.shortList)
//            assertEquals(listOf("a", "b", "c"), primitives.nullableStringList)
//            assertEquals(listOf(1, 2, 3), primitives.nullableIntList)
//            assertEquals(listOf(1000000000000L, 2000000000000L, 3000000000000L), primitives.nullableLongList)
//            assertEquals(listOf(1.1, 2.2, 3.3), primitives.nullableDoubleList)
//            assertEquals(listOf(1.1f, 2.2f, 3.3f), primitives.nullableFloatList)
//            assertEquals(listOf(true, false, true), primitives.nullableBooleanList)
//            assertEquals(listOf(1.toByte(), 2.toByte(), 3.toByte()), primitives.nullableByteList)
//            assertEquals(listOf('a', 'b', 'c'), primitives.nullableCharList)
//            assertEquals(listOf(1.toShort(), 2.toShort(), 3.toShort()), primitives.nullableShortList)
//        }
//    }
//
//    @Test
//    fun testListWithNullValues() {
//        testApplication {
//            routing {
//                install(ServerContentNegotiation) { json() }
//                route<ListPrimitives>("/list-primitives") { value ->
//                    call.respond(HttpStatusCode.OK, value)
//                }
//            }
//
//            val client = createClient {
//                install(ContentNegotiation) { json() }
//            }
//
//            val response = client.get("/list-primitives") {
//                url.parameters.appendAll("stringList", listOf("a", "b", "c"))
//                url.parameters.appendAll("intList", listOf("1", "2", "3"))
//                url.parameters.appendAll("longList", listOf("1000000000000", "2000000000000", "3000000000000"))
//                url.parameters.appendAll("doubleList", listOf("1.1", "2.2", "3.3"))
//                url.parameters.appendAll("floatList", listOf("1.1", "2.2", "3.3"))
//                url.parameters.appendAll("booleanList", listOf("true", "false", "true"))
//                url.parameters.appendAll("byteList", listOf("1", "2", "3"))
//                url.parameters.appendAll("charList", listOf("a", "b", "c"))
//                url.parameters.appendAll("shortList", listOf("1", "2", "3"))
//                // Not providing nullableStringList and others to test null handling
//            }
//
//            // In the current implementation, nullable lists are treated as required fields
//            // So when they're not provided, the server returns a 400 Bad Request
//            assertEquals(HttpStatusCode.BadRequest, response.status)
//        }
//    }
//
//    @Test
//    fun testMissingRequiredList() {
//        testApplication {
//            routing {
//                install(ServerContentNegotiation) { json() }
//                route<ListPrimitives>("/list-primitives") { value ->
//                    call.respond(HttpStatusCode.OK, value)
//                }
//            }
//
//            val client = createClient {
//                install(ContentNegotiation) { json() }
//            }
//
//            val response = client.get("/list-primitives") {
//                // Not providing stringList which is required
//                url.parameters.appendAll("intList", listOf("1", "2", "3"))
//                url.parameters.appendAll("longList", listOf("1000000000000", "2000000000000", "3000000000000"))
//                url.parameters.appendAll("doubleList", listOf("1.1", "2.2", "3.3"))
//                url.parameters.appendAll("floatList", listOf("1.1", "2.2", "3.3"))
//                url.parameters.appendAll("booleanList", listOf("true", "false", "true"))
//                url.parameters.appendAll("byteList", listOf("1", "2", "3"))
//                url.parameters.appendAll("charList", listOf("a", "b", "c"))
//                url.parameters.appendAll("shortList", listOf("1", "2", "3"))
//            }
//            assertEquals(HttpStatusCode.BadRequest, response.status)
//        }
//    }
//}
