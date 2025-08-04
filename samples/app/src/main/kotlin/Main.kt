import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.EndpointFactory
import io.github.nomisrev.typedapi.body
import io.github.nomisrev.typedapi.header
import io.github.nomisrev.typedapi.query
import kotlin.reflect.full.allSuperclasses
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberExtensionFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.declaredMembers

@Endpoint("/bloop")
class MyEndpoint(api: EndpointAPI) {
    val name by api.query<String>()
    val age by api.query<Int>()
    val header by api.header<String>()
    val body by api.body<String>()
}

fun main() {
    val x: EndpointFactory<MyEndpoint> = MyEndpoint.Companion
    x.create { path, fn ->
        println(path)
    }

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
