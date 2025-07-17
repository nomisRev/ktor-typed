// DIRECTIVES: FULL_JDK

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI

@Endpoint("/my/path")
class NoInput(api: EndpointAPI)

fun box(): String {
    val value = NoInput()
    val path = value.path()
    return if(path == "/my/path") "OK" else path
}
