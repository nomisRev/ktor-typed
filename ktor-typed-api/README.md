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
        val query by query<String>()
        val limit by query<Int>()
        
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

The validation system provides comprehensive validation for all primitive types and common patterns. Validations are chainable and provide clear error messages.

### Numeric Validations

#### Integer Validations
```kotlin
routing {
    post("/users/{userId}") {
        val userId by path<Int>(
            validation = Validation.int()
                .min(1, "User ID must be positive")
                .max(1000000, "User ID must be less than 1 million")
        )
        
        val age by query<Int>(
            validation = Validation.int()
                .range(0, 150, "Age must be between 0 and 150")
                .positive("Age must be positive")
        )
        
        call.respond("User $userId with age $age")
    }
}
```

#### All Numeric Types
```kotlin
routing {
    post("/analytics/{sessionId}") {
        // Long validation
        val sessionId by path<Long>(
            validation = Validation.long()
                .positive("Session ID must be positive")
        )
        
        // Double validation
        val score by query<Double>(
            validation = Validation.double()
                .range(0.0, 100.0, "Score must be between 0.0 and 100.0")
                .nonNegative("Score cannot be negative")
        )
        
        // Float validation
        val precision by query<Float>(
            validation = Validation.float()
                .min(0.1f, "Precision must be at least 0.1")
        )
        
        // Short validation
        val attempts by query<Short>(
            validation = Validation.short()
                .range(1, 10, "Attempts must be between 1 and 10")
        )
        
        // Byte validation
        val flags by query<Byte>(
            validation = Validation.byte()
                .range(0, 127, "Flags must be between 0 and 127")
        )
        
        call.respond("Analytics recorded")
    }
}
```

### String Validations

#### Basic String Constraints
```kotlin
routing {
    post("/register") {
        val username by query<String>(
            validation = Validation.string()
                .minLength(3, "Username must be at least 3 characters")
                .maxLength(20, "Username must be at most 20 characters")
                .notBlank("Username cannot be blank")
                .alphanumeric("Username must be alphanumeric")
                .lowercase("Username must be lowercase")
        )
        
        call.respond("Registered user: $username")
    }
}
```

#### Advanced String Validations
```kotlin
routing {
    post("/content") {
        val title by query<String>(
            validation = Validation.string()
                .exactLength(50, "Title must be exactly 50 characters")
                .contains("important", message = "Title must contain 'important'")
                .startsWith("Article:", message = "Title must start with 'Article:'")
                .endsWith(".", message = "Title must end with a period")
        )
        
        val category by query<String>(
            validation = Validation.string()
                .oneOf("tech", "science", "business", 
                       message = "Category must be one of: tech, science, business")
        )
        
        val tags by query<String>(
            validation = Validation.string()
                .regex("[a-z,]+", "Tags must be lowercase letters separated by commas")
        )
        
        call.respond("Content created")
    }
}
```

### Common Pattern Validations

#### Email, URL, and UUID
```kotlin
routing {
    post("/profile") {
        // Email validation
        val email by query<String>(
            validation = Validation.email()
        )
        
        // URL validation
        val website by query<String>(
            validation = Validation.url()
        )
        
        // UUID validation
        val apiKey by header<String>(
            name = "X-API-Key",
            validation = Validation.uuid()
        )
        
        call.respond("Profile updated")
    }
}
```

### Boolean Validations
```kotlin
routing {
    get("/settings") {
        val enableNotifications by query<Boolean>(
            validation = Validation.boolean()
                .isTrue("Notifications must be enabled")
        )
        
        val debugMode by query<Boolean>(
            validation = Validation.boolean()
                .isFalse("Debug mode must be disabled in production")
        )
        
        call.respond("Settings retrieved")
    }
}
```

### Collection Validations
```kotlin
routing {
    post("/batch") {
        val items by query<List<String>>(
            validation = Validation<Collection<String>> { it }
                .minSize(1, "At least one item is required")
                .maxSize(100, "Maximum 100 items allowed")
                .notEmpty("Items list cannot be empty")
        )
        
        call.respond("Batch processed")
    }
}
```

### Chained Validations
```kotlin
routing {
    post("/complex") {
        val complexField by query<String>(
            validation = Validation.string()
                .minLength(5, "Too short")
                .maxLength(50, "Too long")
                .regex("[a-zA-Z0-9_-]+", "Invalid characters")
                .notBlank("Cannot be blank")
                .contains("_", message = "Must contain underscore")
        )
        
        call.respond("Complex validation passed")
    }
}
```

### Available Validation Functions

#### Numeric Types (Int, Long, Double, Float, Short, Byte)
- `min(value, message)` - Minimum value constraint
- `max(value, message)` - Maximum value constraint  
- `range(min, max, message)` - Range constraint
- `positive(message)` - Must be positive (> 0)
- `nonNegative(message)` - Must be non-negative (>= 0)
- `negative(message)` - Must be negative (< 0)

#### String Type
- `minLength(min, message)` - Minimum length
- `maxLength(max, message)` - Maximum length
- `length(min, max, message)` - Length range
- `exactLength(length, message)` - Exact length
- `notEmpty(message)` - Not empty string
- `notBlank(message)` - Not blank (not empty or whitespace)
- `regex(pattern, message)` - Regular expression match
- `contains(substring, ignoreCase, message)` - Contains substring
- `startsWith(prefix, ignoreCase, message)` - Starts with prefix
- `endsWith(suffix, ignoreCase, message)` - Ends with suffix
- `oneOf(values, ignoreCase, message)` - One of specified values
- `alphanumeric(message)` - Only alphanumeric characters
- `alphabetic(message)` - Only alphabetic characters
- `numeric(message)` - Only numeric characters
- `lowercase(message)` - Must be lowercase
- `uppercase(message)` - Must be uppercase

#### Boolean Type
- `isTrue(message)` - Must be true
- `isFalse(message)` - Must be false

#### Collection Types
- `minSize(min, message)` - Minimum collection size
- `maxSize(max, message)` - Maximum collection size
- `size(min, max, message)` - Collection size range
- `exactSize(size, message)` - Exact collection size
- `notEmpty(message)` - Collection not empty

#### Common Patterns
- `Validation.email()` - Email address validation
- `Validation.url()` - HTTP/HTTPS URL validation
- `Validation.uuid()` - UUID format validation

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
    implementation(project(":ktor-typed-api"))
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