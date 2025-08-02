// DIRECTIVES: FULL_JDK

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.header

@Endpoint("/api")
class Header(api: EndpointAPI) {
    val authorization by api.header<String>()
}

fun box(): String {
    val value = Header("Bearer token123")
    val map = buildMap<Any?, Input<Any?>> {
        value.header { any, input ->
            put(any, input)
        }
    }
    
    // Check that we have exactly one header parameter
    if (map.size != 1) return "Expected 1 header parameter, got ${map.size}"
    
    // Check that the header parameter has the correct value
    val headerValue = map.entries.firstOrNull()?.key
    return if (headerValue == "Bearer token123") "OK" else "Expected 'Bearer token123', got '$headerValue'"
}