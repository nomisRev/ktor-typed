package io.github.nomisrev.typedapi

import io.ktor.client.call.body
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.github.nomisrev.typedapi.ktor.get
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.response.respondText
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

@Endpoint(path = "/profile/{profileId}")
class ProfileApi(api: EndpointAPI)  {
    val profileId: Int by api.path<Int>(
        validation = Validation.int().positive("Id needs to be positive")
    )
    val name by api.query<String>()
    val email by api.query<String?>(
        info = Info(description = "Optional email")
    )
    val userAgent by api.header<String>()
    val json by api.body<TestBody>(
        info = Info(
            example = example(TestBody("my custom body")),
        ),
    )
}

@Serializable
data class TestBody(val value: String)

@Serializable
data class Profile(val id: Int, val name: String, val email: String?, val userAgent: String, val json: TestBody)

class Test {
    @Test
    fun example() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            get(ProfileApi) { api ->
                call.respond(Profile(api.profileId, api.name, api.email, api.userAgent, api.json))
            }
        }
        createClient {
            install(ClientContentNegotiation) { json() }
        }.get(ProfileApi(1, "name", null, "userAgent", TestBody("test")))
            .body<Profile>()
    }
}
