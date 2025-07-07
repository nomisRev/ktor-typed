# Ktor Typed API

This module provides a Ktor-specific implementation of the Typed API framework.

## Overview

The Ktor Typed API module extends the core Typed API module to provide integration with Ktor's server and client functionality. It allows you to define type-safe endpoints using a DSL and automatically handles parameter extraction, validation, and serialization.

## Features

- Type-safe endpoint definitions
- Automatic parameter extraction from path, query, headers, and body
- Integration with Ktor's routing system
- Client-side request building
- Validation support

## Usage

### Server-side

```kotlin
class UserApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
    val token: String by api.header<String>()
}

// In your Ktor application
routing {
    route("/users/{id}", HttpMethod.Get, ::UserApi) { api ->
        // Access typed parameters
        val user = userService.getUser(api.id)
        call.respond(user)
    }
}
```

### Client-side

```kotlin
@Serializable
data class UserParams(val id: Int, val name: String, val token: String) {
    fun request(): Request<UserParams, UserApi> = 
        Request(this, ::UserApi)
}

// Make a request
val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}

val params = UserParams(123, "test-name", "auth-token")
val response = client.get("/users/{id}", params.request())
    .body<UserResponse>()
```

## Dependencies

This module depends on:
- The core `typed-api` module
- Ktor server and client libraries