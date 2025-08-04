# TypedApi

A type-safe API framework for HTTP applications, inspired by Tapir and FastAPI, providing automatic parameter
extraction, validation, and serialization.

## Overview

TypedApi is a library that enables you to define type-safe APIs in Kotlin with a focus on:

- **Type Safety**: Compile-time type checking for all API parameters
- **Automatic Validation**: Built-in validation for parameters with clear error messages
- **Clean Syntax**: Property delegation for parameter extraction
- **1st class Compiler Support**: Reduce boilerplate and automatically implement interfaces and behavior with IDE support.
- **Framework Integration**: Seamless integration with Ktor, and Spring WebFlux server and client

The library consists of several modules:

1. **typed-api**: Core module with endpoint definitions and validation
2. **compiler-plugin (& gradle-plugin):** Compiler plugin that enables typesafe client, docs support, and more.
3. **ktor-typed-api**: Ktor-specific implementation
4. **spring-webflux-typed-api**: Spring WebFlux-specific implementation
5. **typed-api-docs**: OpenAPI documentation generation
6. **parser**: OpenAPI model classes and parsing


## Installation

TODO: Not yet released


## Basic Usage

### Defining an API Endpoint

```kotlin
@Endpoint("/users/{userId}")
class UserApi(api: EndpointAPI) {
    val userId by api.path<Int>(
        validation = Validation.int().min(1, "User ID must be positive")
    )
    val name by api.query<String>()
    val token by api.header<String>()
    val userData by api.body<UserData>()
}

@Serializable
data class UserData(val email: String, val age: Int)
```

### Server-side Implementation

#### Ktor

```kotlin
routing {
    post(UserApi) { api ->
        // Access typed parameters
        val user = userService.createUser(id = api.userId, name = api.name, token = api.token, data = api.userData)
        call.respond(HttpStatusCode.OK, user)
    }
}
```

#### Spring WebFlux

```kotlin
router {
    POST(UserApi) { api ->
        // Access typed parameters
        val user = userService.createUser(id = api.userId, name = api.name, token = api.token, data = api.userData)
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(ServerTestResponse(id = api.id, name = api.name, message = "Success"))
    }
}
```

### Client-side Usage

We construct a value using the same `UserApi` type we used for the server, and use it for making a client request.
The _new_ construct, and implementation, is automatically implemented by the compiler plugin.

#### Ktor

```kotlin
suspend fun HttpClient.postUser(): UserResponse =
    post(UserApi(
        userId = 123,
        name = "John Doe",
        token = "auth-token",
        userData = UserData("john@example.com", 30)
    )).body<UserResponse>()
```

## Validation

The library provides a comprehensive validation system:

```kotlin
val age by query<Int>(
    validation = Validation.int()
        .min(18, "Must be at least 18 years old")
        .max(150, "Must be a reasonable age")
)

val email by query<String>(
    validation = Validation.email("Must be a valid email address")
)

val username by query<String>(
    validation = Validation.string()
        .minLength(3, "Username too short")
        .maxLength(20, "Username too long")
        .alphanumeric("Username must be alphanumeric")
)
```

## OpenAPI Documentation (Work In Progress)

The library provides functionality to generate OpenAPI documentation from your endpoints:

```kotlin
// Define your API info
val apiInfo = Info(
    title = "My API",
    version = "1.0.0",
    description = "API for managing users"
)

// Generate OpenAPI documentation
val openAPI = generateOpenAPI<UserApi>(
    info = apiInfo,
    ::UserApi
)

// Convert to JSON or YAML
val json = Json.encodeToString(openAPI)
```

This generates OpenAPI documentation with path items, operations, parameters, and schemas based on your endpoint
definitions.

## Modules

### typed-api

The core module provides the foundation for building type-safe APIs with a focus on validation and metadata support. It
includes:

- Property delegation syntax for parameter extraction
- Comprehensive validation system
- Rich metadata for documentation

### ktor-typed-api

This module provides Ktor-specific implementation of the Typed API framework, including:

- Integration with Ktor's routing system
- Client-side request building
- Server-side parameter extraction

### spring-webflux-typed-api

This module provides Spring WebFlux-specific implementation of the Typed API framework, including:

- Integration with Spring WebFlux's routing system
- Client-side request building with WebClient
- Server-side parameter extraction

### typed-api-docs

This module provides functionality to generate OpenAPI documentation from Typed API endpoints:

- Creates OpenAPI specifications with path items, operations, parameters, and schemas
- Supports documenting endpoints with metadata
- Integrates with the parser module for OpenAPI model classes

### parser

This module contains OpenAPI model classes and parsing functionality:

- Provides data classes for OpenAPI 3.x specification
- Supports JSON and YAML parsing
- Used by the typed-api-docs module for documentation generation

## License

[Apache 2.0](LICENSE)
