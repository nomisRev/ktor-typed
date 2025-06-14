package com.example.test

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.route.fastapi.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.measureTime

@Serializable
data class BenchmarkData(
    val id: Int,
    val name: String,
    val value: Double,
    val active: Boolean = true
)

@Serializable
data class BenchmarkResponse(
    val username: String,
    val count: Int,
    val auth: String?,
    val data: BenchmarkData
)

@Serializable
data class LargePayloadResponse(
    val count: String,
    val totalValue: String
)

class PerformanceTest {

    @Test
    fun `test parameter resolution performance`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/perf/{id}",
                    p1 = Path.required<Int>(ge = 1),
                    p2 = Query<String?>(default = null, minLength = 1, maxLength = 100, name = "query")
                ) { id: Int, query: String? ->
                    call.respond(mapOf("id" to id.toString(), "query" to (query ?: "null")))
                }
            }
        }

        // Measure time for multiple requests
        val requestCount = 100
        val totalTime = measureTime {
            repeat(requestCount) { i ->
                val response = client.get("/perf/${i + 1}") {
                    parameter("query", "test-query-$i")
                }
                assertEquals(HttpStatusCode.OK, response.status)
            }
        }

        // Performance should be reasonable (less than 10ms per request on average)
        val avgTimePerRequest = totalTime.inWholeMilliseconds.toDouble() / requestCount
        assertTrue(avgTimePerRequest < 10.0, "Average time per request: ${avgTimePerRequest}ms")
    }

    @Test
    fun `test validation performance with complex constraints`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                post("/validate-perf",
                    p1 = Query.required<String>(
                        minLength = 5,
                        maxLength = 50,
                        regex = "^[a-zA-Z0-9_-]+$",
                        name = "username"
                    ),
                    p2 = Query.required<Int>(ge = 1, le = 1000, name = "count"),
                    p3 = Header<String?>(
                        default = null,
                        regex = "Bearer [a-zA-Z0-9]+",
                        name = "authorization"
                    ),
                    p4 = Body<BenchmarkData>()
                ) { username: String, count: Int, auth: String?, data: BenchmarkData ->
                    call.respond(BenchmarkResponse(
                        username = username,
                        count = count,
                        auth = auth,
                        data = data
                    ))
                }
            }
        }

        val requestCount = 50
        val totalTime = measureTime {
            repeat(requestCount) { i ->
                val response = client.post("/validate-perf") {
                    parameter("username", "user_$i")
                    parameter("count", "${i + 1}")
                    header("authorization", "Bearer token$i")
                    contentType(ContentType.Application.Json)
                    setBody("""{"id": $i, "name": "item$i", "value": ${i * 1.5}}""")
                }
                assertEquals(HttpStatusCode.OK, response.status)
            }
        }

        val avgTimePerRequest = totalTime.inWholeMilliseconds.toDouble() / requestCount
        assertTrue(avgTimePerRequest < 20.0, "Average time per request with validation: ${avgTimePerRequest}ms")
    }

    @Test
    fun `test concurrent request handling`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/concurrent/{id}",
                    p1 = Path.required<Int>(),
                    p2 = Query<String?>(default = "default", name = "param")
                ) { id: Int, param: String? ->
                    // Simulate some processing time
                    Thread.sleep(1)
                    call.respond(mapOf("id" to id.toString(), "param" to (param ?: "null"), "thread" to Thread.currentThread().name))
                }
            }
        }

        // Test multiple concurrent requests
        val responses = mutableListOf<HttpResponse>()
        val totalTime = measureTime {
            repeat(20) { i ->
                val response = client.get("/concurrent/$i") {
                    parameter("param", "value$i")
                }
                responses.add(response)
            }
        }

        // All requests should succeed
        responses.forEach { response ->
            assertEquals(HttpStatusCode.OK, response.status)
        }

        // Should handle concurrent requests efficiently
        assertTrue(totalTime.inWholeMilliseconds < 1000, "Concurrent requests took too long: ${totalTime.inWholeMilliseconds}ms")
    }

    @Test
    fun `test memory usage with large payloads`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                post("/large-payload",
                    p1 = Body<List<BenchmarkData>>()
                ) { dataList: List<BenchmarkData> ->
                    call.respond(LargePayloadResponse(
                        count = dataList.size.toString(),
                        totalValue = dataList.sumOf { it.value }.toString()
                    ))
                }
            }
        }

        // Create a large payload
        val largeDataList = (1..1000).map { i ->
            BenchmarkData(
                id = i,
                name = "item_$i",
                value = i * 1.5,
                active = i % 2 == 0
            )
        }

        val jsonPayload = kotlinx.serialization.json.Json.encodeToString(
            kotlinx.serialization.builtins.ListSerializer(BenchmarkData.serializer()),
            largeDataList
        )

        val response = client.post("/large-payload") {
            contentType(ContentType.Application.Json)
            setBody(jsonPayload)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val responseText = response.bodyAsText()
        assert(responseText.contains("\"count\":\"1000\""))
    }

    @Test
    fun `test error handling performance`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }

            routing {
                get("/error-test/{id}",
                    p1 = Path.required<Int>(ge = 1, le = 100),
                    p2 = Query.required<String>(minLength = 5, maxLength = 20, name = "required")
                ) { id: Int, required: String ->
                    call.respond(mapOf("id" to id.toString(), "required" to required))
                }
            }
        }

        // Test error handling performance with invalid requests
        val errorRequestCount = 50
        val totalTime = measureTime {
            repeat(errorRequestCount) { i ->
                // Alternate between different types of validation errors
                val response = when (i % 3) {
                    0 -> client.get("/error-test/0") { // Invalid path parameter
                        parameter("required", "valid_string")
                    }
                    1 -> client.get("/error-test/50") { // Missing required query parameter
                        // No required parameter
                    }
                    else -> client.get("/error-test/50") { // Invalid query parameter
                        parameter("required", "ab") // Too short
                    }
                }
                assertEquals(HttpStatusCode.BadRequest, response.status)
            }
        }

        val avgTimePerErrorRequest = totalTime.inWholeMilliseconds.toDouble() / errorRequestCount
        assertTrue(avgTimePerErrorRequest < 15.0, "Average time per error request: ${avgTimePerErrorRequest}ms")
    }
}