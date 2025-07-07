# Spring WebFlux Typed API

This module provides a Spring WebFlux-specific implementation of the Typed API framework.

## Overview

The Spring WebFlux Typed API module extends the core Typed API module to provide integration with Spring WebFlux's server and client functionality. It allows you to define type-safe endpoints using a DSL and automatically handles parameter extraction, validation, and serialization.

## Features

- Type-safe endpoint definitions
- Automatic parameter extraction from path, query, headers, and body
- Integration with Spring WebFlux routing system
- Client-side request building with WebClient
- Validation support

## Usage

### Server-side

```kotlin
class UserApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
    val token: String by api.header<String>()
}

// In your Spring WebFlux application
@Bean
fun routes() = router {
    route("/users/{id}", HttpMethod.GET, ::UserApi) { api ->
        // Access typed parameters
        val user = userService.getUser(api.id)
        ServerResponse.ok().bodyValue(user)
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
val client = WebClient.builder().build()

val params = UserParams(123, "test-name", "auth-token")
val response = client.get()
    .uri("/users/{id}")
    .bodyValue(params.request())
    .retrieve()
    .bodyToMono<UserResponse>()
    .block()
```

## Dependencies

This module depends on:
- The core `typed-api` module
- Spring Boot WebFlux starter
- Jackson Kotlin module for JSON serialization