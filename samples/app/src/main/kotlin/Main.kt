import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.body
import io.github.nomisrev.typedapi.header
import io.github.nomisrev.typedapi.query
import io.ktor.http.ContentType.Text

@Endpoint("/")
class MyEndpoint(api: EndpointAPI) {
    val name by api.query<String>()
    val age by api.query<Int>()
    val header by api.header<String>()
    val body by api.body<String>(Text.Plain)
}

fun main() {
    println("MAIN STARTED!")
    val value = MyEndpoint("Simon", 32, "MY_HEADER", "body")
    println(value.path())
    value.query() { any, input ->
        println(any)
        println(input)
    }
    value.header { any, input ->
        println(any)
        println(input)
    }
    value.body { any, input ->
        println(any)
        println(input)
    }
    println("MAIN FINISHED!")
}
