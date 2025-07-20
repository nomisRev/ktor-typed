// RUN_PIPELINE_TILL: BACKEND
// DIAGNOSTICS: DUPLICATE_PARAMETER_NAME

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.query
import io.github.nomisrev.typedapi.path

// This endpoint has two parameters with the same name but different types
@Endpoint("/users/{id}")
class UserEndpoint(api: EndpointAPI) {
    val id by api.path<Int>()
    val id by api.query<String>() // Duplicate parameter name, should cause an error
}

fun box(): String {
    val value = UserEndpoint(1, "test")
    return "OK"
}