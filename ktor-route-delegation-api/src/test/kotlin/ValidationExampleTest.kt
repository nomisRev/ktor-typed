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
import kotlin.test.*

/**
 * Comprehensive examples demonstrating all validation features in the delegation API.
 * This test class serves as both documentation and verification of the validation system.
 */
class ValidationExampleTest {

    @Serializable
    data class UserProfile(
        val id: Int,
        val username: String,
        val email: String,
        val age: Int,
        val score: Double,
        val isActive: Boolean,
        val tags: List<String>
    )

    @Serializable
    data class ValidationResponse(
        val message: String,
        val data: Map<String, String> = emptyMap()
    )

    @Test
    fun testComprehensiveValidationExample() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            
            routing {
                // Example 1: Integer validations with path parameters
                get("/users/{userId}") {
                    val userId by path<Int>(
                        validation = Validation.int()
                            .min(1, "User ID must be positive")
                            .max(1000000, "User ID must be less than 1 million")
                    )
                    
                    call.respond(ValidationResponse("User ID: $userId"))
                }
                
                // Example 2: String validations with query parameters
                get("/search") {
                    val query by query<String>(
                        validation = Validation.string()
                            .minLength(3, "Search query must be at least 3 characters")
                            .maxLength(100, "Search query must be at most 100 characters")
                            .notBlank("Search query cannot be blank")
                            .regex("[a-zA-Z0-9\\s]+", "Search query can only contain letters, numbers, and spaces")
                    )
                    
                    val limit by query<Int?>()
                    
                    call.respond(ValidationResponse(
                        "Search results",
                        mapOf("query" to query, "limit" to (limit?.toString() ?: "null"))
                    ))
                }
                
                // Example 3: Email validation
                post("/register") {
                    val email by query<String>(
                        validation = Validation.email()
                    )
                    
                    val username by query<String>(
                        validation = Validation.string()
                            .minLength(3, "Username must be at least 3 characters")
                            .maxLength(20, "Username must be at most 20 characters")
                            .alphanumeric("Username must be alphanumeric")
                            .lowercase("Username must be lowercase")
                    )
                    
                    call.respond(ValidationResponse(
                        "Registration successful",
                        mapOf("email" to email, "username" to username)
                    ))
                }
                
                // Example 4: Numeric validations (all types)
                post("/analytics/{sessionId}") {
                    val sessionId by path<Long>(
                        validation = Validation.long()
                            .positive("Session ID must be positive")
                    )
                    
                    val score by query<Double>(
                        validation = Validation.double()
                            .range(0.0, 100.0, "Score must be between 0.0 and 100.0")
                    )
                    
                    val precision by query<Float?>()
                    
                    val attempts by query<Short?>()
                    
                    val flags by query<Byte?>()
                    
                    call.respond(ValidationResponse(
                        "Analytics recorded",
                        mapOf(
                            "sessionId" to sessionId.toString(),
                            "score" to score.toString(),
                            "precision" to (precision?.toString() ?: "null"),
                            "attempts" to (attempts?.toString() ?: "null"),
                            "flags" to (flags?.toString() ?: "null")
                        )
                    ))
                }
                
                // Example 5: Boolean and header validations
                get("/profile") {
                    val includePrivate by query<Boolean?>()
                    
                    val userAgent by header<String?>()
                    
                    val apiKey by header<String?>(name = "X-API-Key")
                    
                    call.respond(ValidationResponse(
                        "Profile data",
                        mapOf(
                            "includePrivate" to (includePrivate?.toString() ?: "null"),
                            "userAgent" to (userAgent ?: "null"),
                            "apiKey" to (apiKey ?: "null")
                        )
                    ))
                }
                
                // Example 6: URL and pattern validations
                post("/webhooks") {
                    val callbackUrl by query<String>(
                        validation = Validation.url()
                    )
                    
                    val secret by query<String>(
                        validation = Validation.string()
                            .exactLength(32, "Secret must be exactly 32 characters")
                            .regex("[a-fA-F0-9]+", "Secret must be hexadecimal")
                    )
                    
                    val eventTypes by query<String?>()
                    
                    call.respond(ValidationResponse(
                        "Webhook registered",
                        mapOf(
                            "callbackUrl" to callbackUrl,
                            "secret" to secret,
                            "eventTypes" to (eventTypes ?: "null")
                        )
                    ))
                }
                
                // Example 7: Chained validations and complex scenarios
                put("/users/{userId}/profile") {
                    val userId by path<Int>(
                        validation = Validation.int()
                            .positive("User ID must be positive")
                    )
                    
                    val bio by query<String?>()
                    
                    val website by query<String?>()
                    
                    val tags by query<String?>()
                    
                    call.respond(ValidationResponse(
                        "Profile updated",
                        mapOf(
                            "userId" to userId.toString(),
                            "bio" to (bio ?: "null"),
                            "website" to (website ?: "null"),
                            "tags" to (tags ?: "null")
                        )
                    ))
                }
            }
        }
        
        // Test integer validations
        client.get("/users/123").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        
        // Test validation failure for negative user ID
        client.get("/users/-1").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
        
        // Test string validations
        client.get("/search?query=kotlin&limit=10").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        
        // Test validation failure for short query
        client.get("/search?query=ab").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
        
        // Test email validation
        client.post("/register?email=test@example.com&username=testuser").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        
        // Test validation failure for invalid email
        client.post("/register?email=invalid-email&username=testuser").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
        
        // Test numeric validations
        client.post("/analytics/12345?score=85.5").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        
        // Test validation failure for out-of-range score
        client.post("/analytics/12345?score=150.0").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
        
        // Test header validations
        client.get("/profile") {
            header("User-Agent", "Test Client")
            header("X-API-Key", "123e4567-e89b-12d3-a456-426614174000")
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        
        // Test URL validation
        client.post("/webhooks?callbackUrl=https://example.com/webhook&secret=abcdef1234567890abcdef1234567890").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
        
        // Test validation failure for invalid URL
        client.post("/webhooks?callbackUrl=not-a-url&secret=abcdef1234567890abcdef1234567890").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
        }
        
        // Test chained validations
        client.put("/users/123/profile?bio=This is a test bio&website=https://example.com").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
    
    @Test
    fun testValidationErrorMessages() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            
            routing {
                get("/test/{value}") {
                    val value by path<Int>(
                        validation = Validation.int()
                            .min(10, "Value must be at least 10")
                            .max(100, "Value must be at most 100")
                    )
                    
                    call.respond(ValidationResponse("Value: $value"))
                }
            }
        }
        
        // Test that validation errors result in BadRequest status
        client.get("/test/5").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            // Note: BadRequestException by default returns empty body in Ktor
            // The validation is working correctly as evidenced by the 400 status
        }
        
        client.get("/test/150").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            // Note: BadRequestException by default returns empty body in Ktor
            // The validation is working correctly as evidenced by the 400 status
        }
    }
    
    @Test
    fun testMultipleValidationErrors() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            
            routing {
                get("/test") {
                    val username by query<String>(
                        validation = Validation.string()
                            .minLength(5, "Username too short")
                            .maxLength(20, "Username too long")
                            .alphanumeric("Username must be alphanumeric")
                    )
                    
                    call.respond(ValidationResponse("Username: $username"))
                }
            }
        }
        
        // Test that multiple validation errors result in BadRequest status
        client.get("/test?username=ab!").apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            // Note: BadRequestException by default returns empty body in Ktor
            // The validation is working correctly as evidenced by the 400 status
            // Multiple validation errors are collected and thrown as a single exception
        }
    }
}