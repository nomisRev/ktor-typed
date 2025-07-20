// RUN_PIPELINE_TILL: BACKEND
// DIAGNOSTICS: UNSUPPORTED_PARAMETER_TYPE

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.query

// Define a complex type that shouldn't be supported as a query parameter
class ComplexType(val value: String) {
    override fun toString(): String = value
}

@Endpoint("/api")
class InvalidTypeEndpoint(api: EndpointAPI) {
    // Using a complex type for a query parameter should cause an error
    val complex by api.query<ComplexType>()
}

fun box(): String {
    val value = InvalidTypeEndpoint(ComplexType("test"))
    return "OK"
}