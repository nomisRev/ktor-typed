import io.github.nomisrev.typedapi.DelegateProvider
import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.query

@Endpoint("/")
class MyEndpoint(api: EndpointAPI) {
    // val _name =  api.Query<String>()
    // val _age =  api.Query<String>()
    // where Query is top-level extension function io.github.nomisrev.typedapi.Query

    val name by api.query<String>()
    val age by api.query<Int>()
}

fun main() {
    val value = MyEndpoint("Simon", 32)
    println(value.name)
    println(value.age)
}
