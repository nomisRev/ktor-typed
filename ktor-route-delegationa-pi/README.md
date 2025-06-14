# Ktor Route Delegation API

A delegation-based routing API for Ktor that provides typed parameter extraction with validation and metadata support. This module uses Kotlin's property delegation to create a clean, type-safe way to extract path parameters, query parameters, and headers from HTTP requests.

## Overview

This module provides a delegation-based approach to parameter extraction in Ktor routes, similar to FastAPI's parameter definition style but using Kotlin's property delegation syntax. It supports automatic parameter validation, type conversion, and rich metadata for documentation.

## Key Features

- **Property Delegation Syntax**: Use `by path<T>()`, `by query<T>()`, `by header<T>()` for clean parameter extraction
- **Automatic Type Conversion**: Converts string parameters to the specified types (Int, Long, Double, Boolean, etc.)
- **Built-in Validation**: Numeric constraints (ge, gt, le, lt), string length, regex patterns
- **Optional Parameters**: Query and header parameters are optional by default (return null when missing)
- **Header Name Conversion**: Automatic conversion from camelCase to kebab-case for headers
- **Rich Metadata**: Support for titles, descriptions, examples for documentation
- **Type Safety**: Full compile-time type checking with Kotlin's type system

## Basic Usage

### Path Parameters
```kotlin
routing {
    get("/users/{userId}") {
        val userId by path<Int>()
        call.respond("User ID: $userId")
    }
}
```

### Query Parameters (Optional by Default)
```kotlin
routing {
    get("/search") {
        val query by query<String>()  // Returns null if not provided
        val limit by query<Int>()     // Returns null if not provided
        
        call.respond(mapOf(
            "query" to query,
            "limit" to limit
        ))
    }
}
```

### Header Parameters (Optional by Default)
```kotlin
routing {
    get("/profile") {
        val userAgent by header<String>()  // Looks for "User-Agent" header
        val apiKey by header<String>(name = "X-API-Key")  // Custom header name
        
        call.respond(mapOf(
            "userAgent" to userAgent,
            "apiKey" to apiKey
        ))
    }
}
```

## Parameter Validation

### Numeric Constraints
```kotlin
routing {
    post("/users/{userId}") {
        val userId by path<Int>(
            title = "User ID",
            description = "The unique identifier for the user",
            ge = 1  // Must be >= 1
        )
        
        val age by query<Int>(
            title = "Age",
            description = "User's age",
            ge = 0,
            le = 150
        )
        
        call.respond("User $userId with age $age")
    }
}
```

### String Constraints
```kotlin
routing {
    post("/register") {
        val username by query<String>(
            title = "Username",
            description = "The username for registration",
            minLength = 3,
            maxLength = 20,
            regex = "[a-zA-Z0-9_]+"
        )
        
        call.respond("Registered user: $username")
    }
}
```

## Advanced Features

### Rich Metadata for Documentation
```kotlin
routing {
    get("/items/{itemId}") {
        val itemId by path<Int>(
            title = "Item ID",
            description = "The unique identifier for the item",
            example = 123,
            ge = 1
        )
        
        val includeDetails by query<Boolean>(
            title = "Include Details",
            description = "Whether to include detailed information",
            example = true
        )
        
        call.respond("Item $itemId, details: $includeDetails")
    }
}
```

### Header Name Conversion
```kotlin
routing {
    get("/api/data") {
        // Property name "userAgent" automatically converts to "User-Agent" header
        val userAgent by header<String>(
            title = "User Agent",
            description = "The client user agent string"
        )
        
        // Property name "contentType" automatically converts to "Content-Type" header  
        val contentType by header<String>()
        
        // Disable automatic conversion
        val customHeader by header<String>(
            name = "X-Custom-Header",
            convertUnderscores = false
        )
        
        call.respond(mapOf(
            "userAgent" to userAgent,
            "contentType" to contentType,
            "customHeader" to customHeader
        ))
    }
}
```

### Validation Error Handling

The module automatically validates parameters and throws `BadRequestException` with descriptive messages:

- `"Parameter 'userId' must be greater than or equal to 1, got 0"`
- `"Parameter 'username' must have at least 3 characters, got 2"`
- `"Parameter 'username' does not match the required pattern: [a-zA-Z0-9_]+"`

## Complete Example

```kotlin
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val userAgent: String?
)

fun Application.configureRouting() {
    routing {
        post("/users/{userId}") {
            // Path parameter with validation
            val userId by path<Int>(
                title = "User ID",
                description = "The unique identifier for the user",
                example = 123,
                ge = 1
            )
            
            // Query parameters with validation
            val name by query<String>(
                title = "User Name",
                description = "The name of the user",
                example = "john_doe",
                minLength = 2,
                maxLength = 50,
                regex = "[a-zA-Z0-9_]+"
            )
            
            val email by query<String>(
                title = "Email Address",
                description = "The user's email address",
                example = "john@example.com",
                regex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
            )
            
            // Header parameter (optional)
            val userAgent by header<String>(
                title = "User Agent",
                description = "The client user agent string"
            )
            
            val response = UserResponse(
                id = userId,
                name = name ?: "Unknown",
                email = email ?: "unknown@example.com", 
                userAgent = userAgent
            )
            
            call.respond(response)
        }
    }
}
```

## API Reference

### Parameter Functions

#### `path<T>()`
Extracts a path parameter. Required by default - throws exception if missing.

**Parameters:**
- `name: String?` - Custom parameter name (defaults to property name)
- `title: String?` - Title for documentation
- `description: String?` - Description for documentation  
- `example: T?` - Example value for documentation
- `ge: Number?` - Greater than or equal to (numeric types)
- `gt: Number?` - Greater than (numeric types)
- `le: Number?` - Less than or equal to (numeric types)
- `lt: Number?` - Less than (numeric types)
- `minLength: Int?` - Minimum string length
- `maxLength: Int?` - Maximum string length
- `regex: String?` - Regular expression pattern
- `deprecated: Boolean` - Mark as deprecated

#### `query<T>()`
Extracts a query parameter. Optional by default - returns null if missing.

**Parameters:** Same as `path<T>()` plus:
- All parameters from `path<T>()`

#### `header<T>()`
Extracts a header parameter. Optional by default - returns null if missing.

**Parameters:** Same as `query<T>()` plus:
- `convertUnderscores: Boolean` - Convert underscores to hyphens in header names (default: true)

## Installation

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":ktor-route-delegation-api"))
}
```

## Requirements

- Ktor 3.1.3+
- Kotlin 2.1.21+
- ContentNegotiation plugin for JSON parsing (if using complex request bodies)

## Comparison with Other Approaches

| Feature | Delegation API | FastAPI Style | Traditional Ktor |
|---------|---------------|---------------|------------------|
| Parameter Extraction | `by path<T>()` | Function parameters | `call.parameters[]` |
| Type Safety | ✅ Compile-time | ✅ Compile-time | ❌ Runtime |
| Validation | ✅ Built-in | ✅ Built-in | ❌ Manual |
| Optional Parameters | ✅ Automatic | ✅ Explicit | ❌ Manual |
| Metadata Support | ✅ Rich metadata | ✅ Rich metadata | ❌ None |
| Syntax Style | Property delegation | Function parameters | Manual extraction |

The delegation API provides a clean, Kotlin-idiomatic way to extract and validate HTTP parameters while maintaining type safety and providing rich metadata for documentation generation.