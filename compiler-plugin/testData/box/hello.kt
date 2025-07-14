// DIRECTIVES: FULL_JDK

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.query

@Endpoint("/")
class Hello(api: EndpointAPI) {
    val age by api.query<Int>()
}

fun box(): String {
    Hello(31)
    return "OK"
}
