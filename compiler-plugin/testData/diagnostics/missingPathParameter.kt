// RUN_PIPELINE_TILL: BACKEND
// DIAGNOSTICS: MISSING_PATH_PARAMETER

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.path

// This endpoint has a path parameter 'id' in the URL, but no corresponding property
@Endpoint("/users/{id}")
class UserEndpoint(api: EndpointAPI) {
    // Missing 'id' path parameter
    val name by api.path<String>() // This should cause an error
}

fun box(): String {
    val value = UserEndpoint("John")
    return "OK"
}