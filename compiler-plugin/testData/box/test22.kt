// DIRECTIVES: FULL_JDK

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointFactory
import io.github.nomisrev.typedapi.EndpointAPI

@Endpoint("O")
class Test(api: EndpointAPI)

fun box(): String {
    var s: String = "NOK"
    val rest = Test.create { text, _ ->
        s = text
        "K"
    }
    return s + rest
}