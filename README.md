# Ktor-Tapir

A type-safe API framework for Ktor applications, inspired by Tapir and FastAPI, providing automatic parameter extraction, validation, and serialization.

## Overview

Ktor-Tapir is a library that enables you to define type-safe APIs in Kotlin with a focus on:

- **Type Safety**: Compile-time type checking for all API parameters
- **Automatic Validation**: Built-in validation for parameters with clear error messages
- **Clean Syntax**: Property delegation for parameter extraction
- **Code Generation**: Reduces boilerplate through KSP
- **Framework Integration**: Seamless integration with Ktor server and client

The library consists of three main modules:

1. **typed-api**: Core module with endpoint definitions and validation
2. **ktor-typed-api**: Ktor-specific implementation
3. **typed-api-ksp**: Kotlin Symbol Processing for code generation

## Features

- Type-safe endpoint definitions
- Automatic parameter extraction from path, query, headers, and body
- Comprehensive validation system
- Rich metadata for documentation
- Client-side request building
- Code generation to reduce boilerplate

## Installation

Add the following to your `build.gradle.kts`:

```kotlin
plugins {
    id("com.google.devtools.ksp") version "2.1.21-1.0.15" // For code generation
}

dependencies {
    implementation(project(":typed-api"))
    implementation(project(":ktor-typed-api"))
    ksp(project(":typed-api-ksp"))
}
```

## Basic Usage

### Defining an API Endpoint

```kotlin
@Endpoint
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

```kotlin
routing {
    route("/users/{userId}", HttpMethod.Post, ::UserApi) { api ->
        // Access typed parameters
        val user = userService.createUser(
            id = api.userId,
            name = api.name,
            token = api.token,
            data = api.userData
        )
        call.respond(user)
    }
}
```

### Client-side Usage

```kotlin
// The @Endpoint annotation triggers code generation of this class
val params = User(
    userId = 123,
    name = "John Doe",
    token = "auth-token",
    userData = UserData("john@example.com", 30)
)

// Make a request
val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

val response = client.post("/users/{userId}", params.request())
    .body<UserResponse>()
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

## Modules

### typed-api

The core module provides the foundation for building type-safe APIs with a focus on validation and metadata support. It includes:

- Property delegation syntax for parameter extraction
- Comprehensive validation system
- Rich metadata for documentation

### ktor-typed-api

This module provides Ktor-specific implementation of the Typed API framework, including:

- Integration with Ktor's routing system
- Client-side request building
- Server-side parameter extraction

### typed-api-ksp

This module contains a Kotlin Symbol Processor (KSP) that generates boilerplate code for client support:

- Generates data classes from `@Endpoint` classes
- Creates `request()` functions for client usage
- Maps property names to KProperty1 references

## Requirements

- Kotlin 2.1.21+
- Ktor 3.1.3+
- KSP 2.1.21-1.0.15+ (for code generation)

## License

[Apache 2.0](LICENSE)