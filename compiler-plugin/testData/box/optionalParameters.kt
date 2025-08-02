// DIRECTIVES: FULL_JDK

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.query

@Endpoint("/api/users")
class UserSearchEndpoint(api: EndpointAPI) {
    val name by api.query<String>()
    val limit by api.query<Int?>()
}

fun box(): String {
    // Test with only required parameter
    val endpoint1 = UserSearchEndpoint("john", null)
    
    val map1 = buildMap<Any?, Input<Any?>> {
        endpoint1.query { any, input ->
            put(any, input)
        }
    }
    
    val values1 = map1.entries.associate { (value, input) -> input.name() to value }
    
    if (values1["name"] != "john") return "Expected name='john', got ${values1["name"]}"
    if (values1["limit"] == "null") return "Expected null, got ${values1["limit"]}"

    // Test with all parameters specified
    val endpoint2 = UserSearchEndpoint("alice", 50)
    
    val map2 = buildMap<Any?, Input<Any?>> {
        endpoint2.query { any, input ->
            put(any, input)
        }
    }
    
    val values2 = map2.entries.associate { (value, input) -> input.name() to value }
    
    if (values2["name"] != "alice") return "Expected name='alice', got ${values2["name"]}"
    if (values2["limit"] != 50) return "Expected limit=50, got ${values2["limit"]}"

    return "OK"
}