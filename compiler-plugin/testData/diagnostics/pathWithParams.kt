// RUN_PIPELINE_TILL: BACKEND

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.path

@Endpoint("/path/{param1}/second/{param2}")
class Paths(api: EndpointAPI) {
    val param1 by api.path<Int>()
    val param2 by api.path<String>()
}

fun box(): String {
    val value = Paths(1, "2")
    val path = value.path()
    return if (path == "/path/1/second/2") "OK" else path
}
