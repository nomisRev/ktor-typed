import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.query

@Endpoint("/")
class MyEndpoint(api: EndpointAPI) {
    val name by api.query<String>()
    val age by api.query<Int>()
}

fun main() {
    val value = MyEndpoint("Simon", 32)
    println(value.name)
    println(value.age)
}
