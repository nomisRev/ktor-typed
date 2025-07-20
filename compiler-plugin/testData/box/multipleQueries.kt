// DIRECTIVES: FULL_JDK

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.query

@Endpoint("/search")
class SearchEndpoint(api: EndpointAPI) {
    val query by api.query<String>()
    val page by api.query<Int>()
    val limit by api.query<Int>()
}

fun box(): String {
    val endpoint = SearchEndpoint("kotlin", 1, 10)

    val map = buildMap<Any?, Input<Any?>> {
        endpoint.query { any, input ->
            put(any, input)
        }
    }

    // Check that we have exactly three query parameters
    if (map.size != 3) return "Expected 3 query parameters, got ${map.size}"

    // Extract values from the map
    val values = map.entries.map { (key, value) -> key }

    // Check that all expected values are present
    if (!values.contains("kotlin")) return "Missing query parameter 'kotlin'"
    if (!values.contains(1)) return "Missing query parameter '1'"
    if (!values.contains(10)) return "Missing query parameter '10'"

    return "OK"
}