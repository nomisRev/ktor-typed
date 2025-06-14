import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.Serializable
import kotlin.test.*

@Serializable
data class TestItem(
    val name: String,
    val description: String? = null,
    val price: Double,
    val tax: Double? = null
)

@Serializable
data class TestResponse(
    val itemId: Int,
    val query: String?,
    val userAgent: String?,
    val xToken: String?,
    val receivedItem: TestItem,
    val customHeader: String? = null
)

@Serializable
data class SimpleResponse(
    val message: String,
    val id: Int
)

class DelegationAPITest {

    @Test
    fun `test fastPut with all parameter types`() = testApplication {
        application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
            routing {
                put("/items/{itemId}") {
                    val itemId by path<Int>()
                    val q by query<String>()
                    val userAgent by header<String>(name = "User-Agent")
                    val xToken by header<String>(name = "X-Token")
                    val customHeader by header<String>(name = "Custom-Header")
                    val item = call.receive<TestItem>()

                    val response = TestResponse(
                        itemId = itemId,
                        query = q,
                        userAgent = userAgent,
                        xToken = xToken,
                        receivedItem = item,
                        customHeader = customHeader
                    )
                    call.respond(response)
                }
            }
        }

        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

        val testItem = TestItem(
            name = "Test Item",
            description = "A test item",
            price = 29.99,
            tax = 2.99
        )

        val response = client.put("/items/123") {
            contentType(ContentType.Application.Json)
            parameter("q", "test-query")
            header("User-Agent", "Test-Agent/1.0")
            header("X-Token", "secret-token")
            header("Custom-Header", "custom-value")
            setBody(testItem)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.body<TestResponse>()
        assertEquals(123, responseBody.itemId)
        assertEquals("test-query", responseBody.query)
        assertEquals("Test-Agent/1.0", responseBody.userAgent)
        assertEquals("secret-token", responseBody.xToken)
        assertEquals("custom-value", responseBody.customHeader)
        assertEquals(testItem, responseBody.receivedItem)
    }

    @Test
    fun `test fastPut with invalid path parameter type returns error`() = testApplication {
        application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
            routing {
                put("/items/{itemId}") {
                    val itemId by path<Int>()
                    val item = call.receive<TestItem>()

                    val response = SimpleResponse("Item $itemId processed", itemId)
                    call.respond(response)
                }
            }
        }

        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

        val testItem = TestItem(name = "Test", price = 10.0)

        val response = client.put("/items/not-a-number") {
            contentType(ContentType.Application.Json)
            setBody(testItem)
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `test fastPut with optional query parameters`() = testApplication {
        application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
            routing {
                put("/items/{itemId}") {
                    val itemId by path<Int>()
                    val optionalQuery by query<String?>()
                    val item = call.receive<TestItem>()

                    val response = TestResponse(
                        itemId = itemId,
                        query = optionalQuery,
                        userAgent = null,
                        xToken = null,
                        receivedItem = item
                    )
                    call.respond(response)
                }
            }
        }

        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

        val testItem = TestItem(name = "Test", price = 10.0)

        // Test without query parameter
        val response1 = client.put("/items/456") {
            contentType(ContentType.Application.Json)
            setBody(testItem)
        }

        assertEquals(HttpStatusCode.OK, response1.status)
        val responseBody1 = response1.body<TestResponse>()
        assertEquals(456, responseBody1.itemId)
        assertNull(responseBody1.query)

        // Test with query parameter
        val response2 = client.put("/items/789") {
            contentType(ContentType.Application.Json)
            parameter("optionalQuery", "present")
            setBody(testItem)
        }

        assertEquals(HttpStatusCode.OK, response2.status)
        val responseBody2 = response2.body<TestResponse>()
        assertEquals(789, responseBody2.itemId)
        assertEquals("present", responseBody2.query)
    }

    @Test
    fun `test fastPut with different data types`() = testApplication {
        application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
            routing {
                // Let's start with a simpler test to debug the issue
                put("/test/{intParam}") {
                    val intParam by path<Int>()
                    val item = call.receive<TestItem>()

                    // Use a response that can be serialized properly
                    val response = TestResponse(
                        itemId = intParam,
                        query = null,
                        userAgent = null,
                        xToken = null,
                        receivedItem = item
                    )
                    call.respond(response)
                }

                // Also test the complex route
                put("/complex/{intParam}/{longParam}/{doubleParam}/{boolParam}") {
                    val intParam by path<Int>()
                    val longParam by path<Long>()
                    val doubleParam by path<Double>()
                    val boolParam by path<Boolean>()
                    val floatQuery by query<Float>()

                    val item = call.receive<TestItem>()

                    // Create a response that can be serialized - convert all to strings for testing
                    val response = mapOf(
                        "intParam" to intParam.toString(),
                        "longParam" to longParam.toString(),
                        "doubleParam" to doubleParam.toString(),
                        "boolParam" to boolParam.toString(),
                        "floatQuery" to floatQuery?.toString(),
                        "itemName" to item.name,
                        "itemPrice" to item.price.toString()
                    )
                    call.respond(response)
                }
            }
        }

        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

        val testItem = TestItem(name = "Test", price = 10.0)

        // Test simple route first
        val simpleResponse = client.put("/test/42") {
            contentType(ContentType.Application.Json)
            setBody(testItem)
        }

        // Debug the simple response
        println("Simple response status: ${simpleResponse.status}")
        if (simpleResponse.status != HttpStatusCode.OK) {
            val errorBody = simpleResponse.body<String>()
            println("Simple error response: $errorBody")
        }

        assertEquals(HttpStatusCode.OK, simpleResponse.status)

        // Test complex route
        val response = client.put("/complex/42/1234567890/3.14159/true") {
            contentType(ContentType.Application.Json)
            parameter("floatQuery", "2.5")
            setBody(testItem)
        }

        // Let's see what we actually get
        println("Response status: ${response.status}")
        if (response.status != HttpStatusCode.OK) {
            val errorBody = response.body<String>()
            println("Error response: $errorBody")
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.body<Map<String, String?>>()
        // All values are now strings, so we can check them directly
        assertEquals("42", responseBody["intParam"])
        assertEquals("1234567890", responseBody["longParam"])
        assertEquals("3.14159", responseBody["doubleParam"])
        assertEquals("true", responseBody["boolParam"])
        assertEquals("2.5", responseBody["floatQuery"])
        assertEquals("Test", responseBody["itemName"])
        assertEquals("10.0", responseBody["itemPrice"])
    }

    @Test
    fun `test fastPut with malformed JSON body returns error`() = testApplication {
        application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
            routing {
                put("/items/{itemId}") {
                    val itemId by path<Int>()
                    val item = call.receive<TestItem>()

                    val response = SimpleResponse("Item $itemId processed", itemId)
                    call.respond(response)
                }
            }
        }

        val response = client.put("/items/123") {
            contentType(ContentType.Application.Json)
            setBody("{invalid json}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `test multiple fastPut routes`() = testApplication {
        application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
            routing {
                put("/items/{itemId}") {
                    val itemId by path<Int>()
                    val item = call.receive<TestItem>()

                    val response = SimpleResponse("Item $itemId updated", itemId)
                    call.respond(response)
                }

                put("/products/{productId}") {
                    val productId by path<Int>()
                    val item = call.receive<TestItem>()

                    val response = SimpleResponse("Product $productId updated", productId)
                    call.respond(response)
                }
            }
        }

        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

        val testItem = TestItem(name = "Test", price = 10.0)

        // Test first route
        val response1 = client.put("/items/123") {
            contentType(ContentType.Application.Json)
            setBody(testItem)
        }

        assertEquals(HttpStatusCode.OK, response1.status)
        val responseBody1 = response1.body<SimpleResponse>()
        assertEquals("Item 123 updated", responseBody1.message)

        // Test second route
        val response2 = client.put("/products/456") {
            contentType(ContentType.Application.Json)
            setBody(testItem)
        }

        assertEquals(HttpStatusCode.OK, response2.status)
        val responseBody2 = response2.body<SimpleResponse>()
        assertEquals("Product 456 updated", responseBody2.message)
    }

    @Test
    fun `test fastPut with complex nested object`() = testApplication {
        @Serializable
        data class Address(val street: String, val city: String, val zipCode: String)

        @Serializable
        data class User(val name: String, val email: String, val address: Address)

        @Serializable
        data class UserResponse(val userId: Int, val user: User, val processed: Boolean = true)

        application {
            install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
            routing {
                put("/users/{userId}") {
                    val userId by path<Int>()
                    val user = call.receive<User>()

                    val response = UserResponse(userId, user)
                    call.respond(response)
                }
            }
        }

        val client = createClient {
            install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
                json()
            }
        }

        val address = Address("123 Main St", "Anytown", "12345")
        val user = User("John Doe", "john@example.com", address)

        val response = client.put("/users/42") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.body<UserResponse>()
        assertEquals(42, responseBody.userId)
        assertEquals(user, responseBody.user)
        assertTrue(responseBody.processed)
    }
}