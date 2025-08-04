package io.github.nomisrev.typedapi

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.github.nomisrev.typedapi.ktor.get
import io.ktor.client.call.body
import io.ktor.http.ContentType.Application.Json
import io.ktor.server.testing.testApplication
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

@Endpoint(path = "/client-test/{id}")
class SimpleClientApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
    val header: String by api.header<String>()
    val body: ClientTestBody by api.body<ClientTestBody>(Json)
}

@Serializable
data class ClientTestBody(val value: String)

@Serializable
data class ClientTestResponse(
    val id: Int,
    val name: String,
    val header: String,
    val body: ClientTestBody
)

@Endpoint(path = "/nullable-test/{id}")
class NullableParamsApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String? by api.query<String?>()
    val header: String? by api.header<String?>()
}

class ClientTest {

    @Test
    fun testClientRequestBuilding() = testApplication {
        routing {
            install(ContentNegotiation) { json() }

            get(SimpleClientApi) { api ->
                call.respond(
                    ClientTestResponse(
                        id = api.id,
                        name = api.name,
                        header = api.header,
                        body = api.body
                    )
                )
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val testBody = ClientTestBody("test-value")
        val testParams = SimpleClientApi(123, "test-name", "test-header", testBody)

        val response = client.get(testParams).body<ClientTestResponse>()

        assertEquals(ClientTestResponse(123, "test-name", "test-header", testBody), response)
    }

    @Test
    fun testClientRequestWithNullableParameters() = testApplication {
        @Serializable
        data class NullableResponse(
            val id: Int,
            val name: String?,
            val header: String?
        )

        routing {
            install(ContentNegotiation) { json() }

            get(NullableParamsApi) { api ->
                call.respond(
                    NullableResponse(
                        id = api.id,
                        name = api.name,
                        header = api.header
                    )
                )
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        // io.github.nomisrev.typedapi.Test with null values
        val nullParams = NullableParamsApi(123, null, null)

        val nullResponse = client.get(nullParams).body<NullableResponse>()

        assertEquals(
            NullableResponse(123, null, null),
            nullResponse
        )

        // io.github.nomisrev.typedapi.Test with non-null values
        val nonNullParams = NullableParamsApi(456, "test-name", "test-header")

        val nonNullResponse = client.get(nonNullParams)
            .body<NullableResponse>()

        assertEquals(
            NullableResponse(456, "test-name", "test-header"),
            nonNullResponse
        )
    }
}
