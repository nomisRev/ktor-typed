package io.github.nomisrev.typedapi

import io.github.nomisrev.typedapi.spring.GET
import kotlinx.serialization.Serializable
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import kotlin.test.Test

@Endpoint(path = "/profile/{profileId}")
class ProfileApi(api: EndpointAPI) {
    val profileId: Int by api.path<Int>()
    val name by api.query<String>()
    val email by api.query<String?>()
    val userAgent by api.header<String>()
}

@Serializable
data class TestBody(val value: String)

class Test {
    @Test
    fun example() {
        val router = router {
            GET(::ProfileApi) { api ->
                ServerResponse.ok().bodyValue(Profile(api.profileId, api.name, api.email, api.userAgent))
            }
        }

        val client = WebTestClient.bindToRouterFunction(router).build()
        
        client.get()
            .uri("/profile/1?name=name")
            .header("User-Agent", "userAgent")
            .exchange()
            .expectStatus().isOk
    }
}