# Ktor Route FastAPI

A FastAPI-inspired routing module for Ktor that provides typed route definitions with parameter metadata and validation, similar to FastAPI's `Path()`, `Query()`, and `Header()` functions.

## Overview

This module brings FastAPI's elegant parameter definition style to Ktor, allowing you to define routes with rich metadata and validation constraints. Unlike traditional Ktor routing, this module uses function parameters with metadata objects that specify validation rules, default values, and documentation.

## Key Features

- **FastAPI-style Parameter Metadata**: Use `Path()`, `Query()`, and `Header()` with validation constraints
- **Automatic Parameter Resolution**: Resolves parameters from path, query, headers, and request body
- **Built-in Validation**: Numeric constraints (ge, gt, le, lt), string length, regex patterns
- **Type Safety**: Full compile-time type checking with Kotlin's type system
- **Request Body Parsing**: Automatic JSON/serialization for complex types
- **Nullable Support**: Optional parameters with proper null handling

## FastAPI Comparison

### Python FastAPI
```python
@app.put("/items/{item_id}")
async def update_item(
    *,
    item_id: int = Path(..., title="The ID of the item to get", ge=1),
    q: Optional[str] = Query(None, min_length=3, max_length=50, description="A query string"),
    item: Item,
    user_agent: Optional[str] = Header(None, description="The user agent of the client"),
    x_token: Optional[str] = Header(None, description="A custom header token")
):
    results = {"item_id": item_id, "item": item}
    if q:
        results.update({"q": q})
    if user_agent:
        results.update({"user_agent": user_agent})
    if x_token:
        results.update({"x_token": x_token})
    return results
```

### Kotlin Ktor FastAPI
```kotlin
routing {
    put("/items/{item_id}",
        p1 = Path.required<Int>(title = "The ID of the item to get", ge = 1),
        p2 = Query<String?>(default = null, minLength = 3, maxLength = 50, description = "A query string"),
        p3 = DefaultParam, // Item will be parsed from body automatically
        p4 = Header<String?>(default = null, description = "The user agent of the client"),
        p5 = Header<String?>(default = null, description = "A custom header token")
    ) { itemId: Int, q: String?, item: Item, userAgent: String?, xToken: String? ->
        
        val results = UpdateItemResponse(
            item_id = itemId,
            item = item,
            q = q,
            user_agent = userAgent,
            x_token = xToken
        )
        call.respond(results)
    }
}
```

## Parameter Types

### Path Parameters
```kotlin
Path.required<Int>(
    title = "The ID of the item",
    description = "Unique identifier for the item",
    ge = 1  // Must be >= 1
)

Path<String>(
    default = "default-id",
    regex = "[a-zA-Z0-9-]+"
)
```

### Query Parameters
```kotlin
Query.required<String>(
    minLength = 3,
    maxLength = 50,
    description = "Search query"
)

Query<Int?>(
    default = null,
    ge = 0,
    le = 100
)
```

### Header Parameters
```kotlin
Header<String?>(
    default = null,
    description = "User agent string",
    convertUnderscores = true  // user_agent -> User-Agent
)

Header.required<String>(
    regex = "Bearer .+",
    description = "Authorization token"
)
```

### Request Body (Complex Types)
```kotlin
// Use DefaultParam for automatic body parsing
put("/items/{id}",
    p1 = Path.required<String>(),
    p2 = DefaultParam  // Item will be parsed from JSON body
) { id: String, item: Item ->
    // Handle the request
}
```

## Validation Features

### Numeric Constraints
- `ge` - Greater than or equal to
- `gt` - Greater than  
- `le` - Less than or equal to
- `lt` - Less than

### String Constraints
- `minLength` - Minimum string length
- `maxLength` - Maximum string length
- `regex` - Regular expression pattern

### Example with Validation
```kotlin
routing {
    post("/users",
        p1 = Query.required<String>(
            minLength = 3,
            maxLength = 20,
            regex = "[a-zA-Z0-9_]+",
            description = "Username"
        )
    ) { username: String ->
        call.respond("Hello, $username!")
    }
}
```

## Usage Examples

### Simple GET Route
```kotlin
routing {
    get("/users/{userId}",
        p1 = Path.required<String>(description = "User ID"),
        p2 = Query<String?>(default = "Unknown", description = "User name")
    ) { userId: String, name: String? ->
        call.respond(mapOf(
            "userId" to userId,
            "name" to name
        ))
    }
}
```

### POST with Body and Validation
```kotlin
@Serializable
data class CreateUserRequest(val name: String, val email: String)

routing {
    post("/users",
        p1 = DefaultParam  // CreateUserRequest from body
    ) { request: CreateUserRequest ->
        // Process the user creation
        call.respond(HttpStatusCode.Created)
    }
}
```

### Complex Route with All Parameter Types
```kotlin
routing {
    put("/items/{itemId}",
        p1 = Path.required<Int>(ge = 1),
        p2 = Query<String?>(default = null, maxLength = 100),
        p3 = DefaultParam, // Request body
        p4 = Header<String?>(default = null),
        p5 = Header<String?>(default = null)
    ) { itemId: Int, filter: String?, updateData: UpdateRequest, userAgent: String?, authToken: String? ->
        // Handle the complex request
    }
}
```

## Error Handling

The module automatically validates parameters and throws `BadRequestException` with descriptive messages:

- Missing required parameters
- Validation constraint violations
- Type conversion errors
- Malformed request bodies

Example error messages:
- `"Parameter 'item_id' must be greater than or equal to 1, got 0"`
- `"Parameter 'username' must have at least 3 characters, got 2"`
- `"Missing required query parameter: search"`

## Installation

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":ktor-route-fastapi"))
}
```

## Configuration

Install ContentNegotiation for request body parsing:

```kotlin
install(ContentNegotiation) {
    json()
}
```

## Comparison with Other Approaches

| Feature | FastAPI (Python) | ktor-route-fastapi | ktor-route-simple | Traditional Ktor |
|---------|------------------|-------------------|-------------------|------------------|
| Parameter Metadata | ✅ Path(), Query(), Header() | ✅ Path(), Query(), Header() | ❌ Annotations only | ❌ Manual extraction |
| Validation | ✅ Built-in | ✅ Built-in | ❌ Manual | ❌ Manual |
| Type Safety | ✅ Runtime | ✅ Compile-time | ✅ Compile-time | ❌ Runtime |
| API Style | Function parameters | Function parameters | Data classes | Manual routing |
| Documentation | Auto-generated | Metadata available | Limited | Manual |

## Requirements

- Ktor 3.1.3+
- Kotlin 2.1.21+
- ContentNegotiation plugin for JSON parsing
- Kotlinx Serialization for data classes

## Examples

See the test files and `Example.kt` for comprehensive usage patterns and real-world scenarios.