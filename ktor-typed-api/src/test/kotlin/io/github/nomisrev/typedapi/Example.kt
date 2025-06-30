package io.github.nomisrev.typedapi

import io.ktor.client.call.body
import io.ktor.http.HttpMethod
import io.github.nomisrev.typedapi.ktor.route
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.github.nomisrev.typedapi.Request
import io.github.nomisrev.typedapi.properties
import io.github.nomisrev.typedapi.ktor.get
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty1
import kotlin.test.Test


class ProfileApi(api: EndpointAPI) {
    val profileId: Int by api.path<Int>()
    val name by api.query<String>()
    val email by api.query<String?>()
    val userAgent by api.header<String>()
    val json by api.body<TestBody>()
}

@Serializable
data class Profile(
    val profileId: Int,
    val name: String,
    val email: String?,
    val userAgent: String,
    val json: TestBody
) {
    fun request(): Request<Profile, ProfileApi> = Request(
        this,
        ::ProfileApi,
        profileProperties
    )
}

private val profileProperties: Map<String, KProperty1<Profile, *>> = properties(
    Profile::profileId,
    Profile::name,
    Profile::email,
    Profile::userAgent,
    Profile::json
)


@Serializable
data class TestBody(val value: String)

class Test {
    @Test
    fun example() = testApplication {
        routing {
            install(ContentNegotiation) { json() }
            route("/profile/{profileId}", HttpMethod.Get, ::ProfileApi) { api ->
                call.respond(Profile(api.profileId, api.name, api.email, api.userAgent, api.json))
            }
        }
        createClient {
            install(ClientContentNegotiation) { json() }
        }.get("/profile/{profileId}", Profile(1, "name", null, "userAgent", TestBody("test")).request())
            .body<Profile>()
    }
}