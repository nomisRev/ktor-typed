import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.Inspectable
import io.github.nomisrev.typedapi.body
import io.github.nomisrev.typedapi.query

@Endpoint("/")
class MyEndpoint(api: EndpointAPI) {
    val name by api.query<String>()
    val age by api.query<Int>()
    val body by api.body<String>()
}

fun main() {
}
