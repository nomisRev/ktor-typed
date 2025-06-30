# Typed API KSP

This module contains a Kotlin Symbol Processor (KSP) that generates boilerplate code for client support in the Typed API library.

## Overview

The KSP processor in this module scans for classes annotated with `@Endpoint` and generates:

1. A data class with matching fields
2. A `request()` function that creates a `Request` object
3. A properties map that maps property names to KProperty1 references

## Example

For a class like:

```kotlin
@Endpoint
class ProfileApi(api: EndpointAPI) {
    val profileId: Int by api.path<Int>()
    val name by api.query<String>()
    val email by api.query<String?>()
    val userAgent by api.header<String>()
    val json by api.body<TestBody>()
}
```

The processor will generate:

```kotlin
@Serializable
data class Profile(
    val profileId: Int,
    val name: String,
    val email: String?,
    val userAgent: String,
    val json: TestBody
) {
    fun request(): Request<Profile, ProfileApi> = Request(
        this,
        ::ProfileApi,
        profileProperties
    )
}

private val profileProperties: Map<String, KProperty1<Profile, *>> = properties(
    Profile::profileId,
    Profile::name,
    Profile::email,
    Profile::userAgent,
    Profile::json
)
```

## Usage

To use this processor, add the KSP plugin to your project and include this module as a KSP dependency:

```kotlin
plugins {
    id("com.google.devtools.ksp") version "..."
}

dependencies {
    ksp(project(":typed-api-ksp"))
}
```

Then annotate your API classes with `@Endpoint` and the processor will generate the boilerplate code during compilation.