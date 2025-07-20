// DIRECTIVES: FULL_JDK

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.Input
import io.github.nomisrev.typedapi.body

data class User(val name: String, val age: Int)

@Endpoint("/users")
class UserEndpoint(api: EndpointAPI) {
    val user by api.body<User>()
}

fun box(): String {
    val testUser = User("John Doe", 30)
    val endpoint = UserEndpoint(testUser)

    val map = buildMap<Any?, Input<Any?>> {
        endpoint.body { any, input ->
            put(any, input)
        }
    }

    // Check that we have exactly one body parameter
    if (map.size != 1) return "Expected 1 body parameter, got ${map.size}"

    val bodyValue = map.entries.firstOrNull()?.key as? User
    if (bodyValue == null) return "Body value is null or not a User"

    return if (bodyValue == testUser) "OK" else "Expected $testUser, got $bodyValue"
}