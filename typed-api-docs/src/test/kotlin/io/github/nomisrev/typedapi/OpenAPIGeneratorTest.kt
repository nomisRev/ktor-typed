package io.github.nomisrev.typedapi

import io.github.nomisrev.openapi.Info
import io.github.nomisrev.openapi.Parameter
import io.github.nomisrev.openapi.ReferenceOr
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

// Define a simple endpoint function
@Endpoint("/users/{id}")
class UserEndpoint(api: EndpointAPI) /* : Documented */ {
    val id by api.path<Int>()

    // TODO alternative option
    // init {
    //   // DSL??
    //   api.document {
    //       // API Doc DSL
    //   }
    // }

    // Or inheritance, allows extra `A : Documented` check, and together with lazy eval it's pretty OK!
    // Can automatically be added by compiler plugin
    // override val info by Info {
    //     returns = oneOf(
    //         HttpStatusCode.OK to body<User>(Json),
    //         HttpStatusCode.NotFound to text
    //     )
    //     ...
    // }
}

class OpenAPIGeneratorTest {

    @Test
    fun testGenerateOpenAPI() {
        // Create a simple API info
        val info = Info(
            title = "Test API",
            version = "1.0.0"
        )

        // Generate OpenAPI documentation
        val openAPI = generateOpenAPI(
            info,
            { api -> UserEndpoint(api) }
        )

        // Verify the OpenAPI object
        assertEquals("Test API", openAPI.info.title)
        assertEquals("1.0.0", openAPI.info.version)

        // Verify the path
        val pathItem = openAPI.paths["/users/{id}"]
        assertNotNull(pathItem)

        // Verify the operation
        val operation = pathItem.get
        assertNotNull(operation)

        // Verify the parameters
        assertEquals(1, operation.parameters.size)
        val parameterRef = operation.parameters[0]
        assertTrue(parameterRef is ReferenceOr.Value)
        val parameter = (parameterRef as ReferenceOr.Value<Parameter>).value
        assertEquals("id", parameter.name)
        assertEquals(Parameter.Input.Path, parameter.input)

        // Print the OpenAPI JSON
        println(openAPI.toJson())
    }
}