package io.github.nomisrev.typedapi

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.testing.testApplication
import io.github.nomisrev.typedapi.Request
import io.github.nomisrev.typedapi.ktor.get
import io.github.nomisrev.typedapi.ktor.route
import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals

// Using the post extension function defined in InputTest.kt

class ClientTestApi(api: EndpointAPI) {
    val id: Int by api.path<Int>()
    val name: String by api.query<String>()
    val header: String by api.header<String>()
    val body: ClientTestBody by api.body<ClientTestBody>()
}

@Serializable
data class ClientTestBody(val value: String)

@Serializable
data class ClientTestParams(
    val id: Int,
    val name: String,
    val header: String,
    val body: ClientTestBody
) {
    fun request(): Request<ClientTestParams, ClientTestApi> =
        Request(this, ::ClientTestApi, clientTestProperties)
}

private val clientTestProperties = properties(
    ClientTestParams::id,
    ClientTestParams::name,
    ClientTestParams::header,
    ClientTestParams::body
)

// Using the io.github.nomisrev.typedapi.properties function defined in InputTest.kt

@Serializable
data class ClientTestResponse(
    val id: Int,
    val name: String,
    val header: String,
    val body: ClientTestBody
)

class ClientTest {

    @Test
    fun testClientRequestBuilding() = testApplication {
        routing {
            install(ContentNegotiation) { json() }

            route("/client-test/{id}", HttpMethod.Get, ::ClientTestApi) { api ->
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
        val testParams = ClientTestParams(123, "test-name", "test-header", testBody)

        val response = client.get("/client-test/{id}", testParams.request())
            .body<ClientTestResponse>()

        assertEquals(123, response.id)
        assertEquals("test-name", response.name)
        assertEquals("test-header", response.header)
        assertEquals(testBody, response.body)
    }

    @Test
    fun testClientRequestWithNullableParameters() = testApplication {
        class NullableParamsApi(api: EndpointAPI) {
            val id: Int by api.path<Int>()
            val name: String? by api.query<String?>()
            val header: String? by api.header<String?>()
        }

        @Serializable
        data class NullableParams(
            val id: Int,
            val name: String?,
            val header: String?
        ) {
            fun request(): Request<NullableParams, NullableParamsApi> {
                val properties = mapOf(
                    "id" to NullableParams::id,
                    "name" to NullableParams::name,
                    "header" to NullableParams::header
                )
                return Request(this, ::NullableParamsApi, properties)
            }
        }

        @Serializable
        data class NullableResponse(
            val id: Int,
            val name: String?,
            val header: String?
        )

        routing {
            install(ContentNegotiation) { json() }

            route("/nullable-test/{id}", HttpMethod.Get, ::NullableParamsApi) { api ->
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
        val nullParams = NullableParams(123, null, null)

        val nullResponse = client.get("/nullable-test/{id}", nullParams.request())
            .body<NullableResponse>()

        assertEquals(123, nullResponse.id)
        assertEquals(null, nullResponse.name)
        assertEquals(null, nullResponse.header)

        // io.github.nomisrev.typedapi.Test with non-null values
        val nonNullParams = NullableParams(456, "test-name", "test-header")

        val nonNullResponse = client.get("/nullable-test/{id}", nonNullParams.request())
            .body<NullableResponse>()

        assertEquals(456, nonNullResponse.id)
        assertEquals("test-name", nonNullResponse.name)
        assertEquals("test-header", nonNullResponse.header)
    }
}
