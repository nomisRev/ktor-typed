package io.github.nomisrev.typedapi

import io.github.nomisrev.openapi.Info
import io.github.nomisrev.openapi.MediaType
import io.github.nomisrev.openapi.OpenAPI
import io.github.nomisrev.openapi.Operation
import io.github.nomisrev.openapi.Parameter
import io.github.nomisrev.openapi.PathItem
import io.github.nomisrev.openapi.ReferenceOr
import io.github.nomisrev.openapi.RequestBody
import io.github.nomisrev.openapi.Response
import io.github.nomisrev.openapi.Responses
import io.github.nomisrev.openapi.Schema
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public inline fun <reified A : Any> generateOpenAPI(
    info: Info,
    vararg endpoints: (EndpointAPI) -> A
): OpenAPI = generateOpenAPI(typeOf<A>(), A::class, info, *endpoints)

/**
 * Generates OpenAPI documentation for an EndpointAPI.
 *
 * @param info The metadata about the API.
 * @param endpoints A list of functions that create endpoints using the EndpointAPI.
 * @return An OpenAPI object containing the documentation for the endpoints.
 */
public fun <A : Any> generateOpenAPI(
    type: KType,
    kClass: KClass<A>,
    info: Info,
    vararg endpoints: (EndpointAPI) -> A
): OpenAPI =
    endpoints.fold(OpenAPI(info = info)) { api, endpoint ->
        val route = collect(type, kClass, block = endpoint)
        api.withPathItem(route.path, createPathItem(route))
    }

/**
 * Creates a PathItem for a Route.
 *
 * @param route The route to create a PathItem for.
 * @return A PathItem containing the operation for the route.
 */
private fun createPathItem(route: Route): PathItem {
    val operation = createOperation(route)
    // TODO for now we'll assume all endpoints are GET operations
    return PathItem(get = operation)
}

/**
 * Creates an Operation for a Route.
 *
 * @param route The route to create an Operation for.
 * @return An Operation containing the parameters and request body for the route.
 */
private fun createOperation(route: Route): Operation {
    val parameters = mutableListOf<ReferenceOr<Parameter>>()
    var requestBody: ReferenceOr<RequestBody>? = null

    for (input in route.inputs) {
        when (input) {
            is Input.Path<*> -> {
                parameters.add(
                    ReferenceOr.Value(
                        Parameter(
                            name = input.name ?: "",
                            input = Parameter.Input.Path,
                            schema = createSchema(input.kClass, input.kType)
                        )
                    )
                )
            }

            is Input.Query<*> -> {
                parameters.add(
                    ReferenceOr.Value(
                        Parameter(
                            name = input.name ?: "",
                            input = Parameter.Input.Query,
                            schema = createSchema(input.kClass, input.kType)
                        )
                    )
                )
            }

            is Input.Header<*> -> {
                parameters.add(
                    ReferenceOr.Value(
                        Parameter(
                            name = input.name ?: "",
                            input = Parameter.Input.Header,
                            schema = createSchema(input.kClass, input.kType)
                        )
                    )
                )
            }

            is Input.Body<*> -> {
                requestBody = ReferenceOr.Value(
                    RequestBody(
                        content = mapOf(
                            "application/json" to MediaType(
                                schema = createSchema(
                                    input.kClass,
                                    input.kType
                                )
                            )
                        )
                    )
                )
            }
        }
    }

    // TODO properly allow documenting responses.
    // For simplicity, we'll use a default response
    val responses = Responses(200, Response(description = "Successful operation"))
    return Operation(parameters = parameters, requestBody = requestBody, responses = responses)
}

/**
 * Creates a Schema for a KClass and KType.
 *
 * @param kClass The KClass to create a Schema for.
 * @param kType The KType to create a Schema for.
 * @return A ReferenceOr<Schema> for the KClass and KType.
 */
// TODO Using KSerializer.descriptor would be really useful...
private fun createSchema(kClass: KClass<*>, kType: KType): ReferenceOr<Schema> {
    // For simplicity, we'll use a basic mapping of Kotlin types to OpenAPI types
    val type = when (kClass) {
        String::class -> Schema.Type.Basic.String
        Int::class, Long::class, Short::class, Byte::class -> Schema.Type.Basic.Integer
        Float::class, Double::class -> Schema.Type.Basic.Number
        Boolean::class -> Schema.Type.Basic.Boolean
        List::class, Array::class, Set::class -> Schema.Type.Basic.Array
        else -> Schema.Type.Basic.Object
    }

    val nullable = kType.isMarkedNullable

    return ReferenceOr.Value(Schema(type = type, nullable = nullable))
}