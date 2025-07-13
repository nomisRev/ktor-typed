// DIRECTIVES: FULL_JDK

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.query

@Endpoint("/")
class Query(api: EndpointAPI) {
    val age by api.query<Int>()
}

fun box(): String {
    val value = Query(32)
    val map = buildMap<Any?, Input<Any?>> {
        value.query { any, input ->
            put(any, input)
        }
    }
    return if (map.size == 1) "OK" else "fail"
}
