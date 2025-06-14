package io.ktor.route.fastapi

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import io.ktor.util.reflect.TypeInfo
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * FastAPI-style PUT route with parameter metadata support.
 *
 * Example usage:
 * ```kotlin
 * routing {
 *     put("/items/{item_id}",
 *         p1 = Path.required<Int>(),
 *         p2 = Query<String?>(),
 *         p3 = Body<Item>(),
 *         p4 = Header<String?>(),
 *         p5 = Header<String?>()
 *     ) { itemId: Int, q: String?, item: Item, userAgent: String?, xToken: String? ->
 *         // Route handler logic
 *         call.respond(mapOf(
 *             "item_id" to itemId,
 *             "q" to q,
 *             "item" to item,
 *             "user_agent" to userAgent,
 *             "x_token" to xToken
 *         ))
 *     }
 * }
 * ```
 */
inline fun <reified P1, reified P2, reified P3, reified P4, reified P5> Route.put(
    path: String,
    p1: Input<P1>,
    p2: Input<P2>,
    p3: Input<P3>,
    p4: Input<P4>,
    p5: Input<P5>,
    crossinline handler: suspend RoutingContext.(P1, P2, P3, P4, P5) -> Unit
) = route(path, HttpMethod.Put) {
    handle {
        try {
            val pathParams = extractPathParameterNames(path)
            val params = resolveFastAPIParameters(
                listOf(
                    ParameterInfo(typeOf<P1>(), determineParameterName(p1, pathParams, 0), p1, pathParams),
                    ParameterInfo(typeOf<P2>(), determineParameterName(p2, pathParams, 1), p2, pathParams),
                    ParameterInfo(typeOf<P3>(), determineParameterName(p3, pathParams, 2), p3, pathParams),
                    ParameterInfo(typeOf<P4>(), determineParameterName(p4, pathParams, 3), p4, pathParams),
                    ParameterInfo(typeOf<P5>(), determineParameterName(p5, pathParams, 4), p5, pathParams)
                )
            )

            @Suppress("UNCHECKED_CAST")
            handler(
                params[0] as P1,
                params[1] as P2,
                params[2] as P3,
                params[3] as P4,
                params[4] as P5
            )
        } catch (e: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad Request")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${e.message}")
        }
    }
}

/**
 * FastAPI-style GET route with parameter metadata support.
 */
inline fun <reified P1, reified P2> Route.get(
    path: String,
    p1: Input<P1>,
    p2: Input<P2>,
    crossinline handler: suspend RoutingContext.(P1, P2) -> Unit
) = route(path, HttpMethod.Get) {
    handle {
        try {
            val pathParams = extractPathParameterNames(path)
            val params = resolveFastAPIParameters(
                listOf(
                    ParameterInfo(typeOf<P1>(), determineParameterName(p1, pathParams, 0), p1, pathParams),
                    ParameterInfo(typeOf<P2>(), determineParameterName(p2, pathParams, 1), p2, pathParams)
                )
            )

            @Suppress("UNCHECKED_CAST")
            handler(params[0] as P1, params[1] as P2)
        } catch (e: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad Request")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${e.message}")
        }
    }
}

/**
 * FastAPI-style POST route with 2 parameters.
 */
inline fun <reified P1, reified P2> Route.post(
    path: String,
    p1: Input<P1>,
    p2: Input<P2>,
    crossinline handler: suspend RoutingContext.(P1, P2) -> Unit
) = route(path, HttpMethod.Post) {
    handle {
        try {
            val pathParams = extractPathParameterNames(path)
            val params = resolveFastAPIParameters(
                listOf(
                    ParameterInfo(typeOf<P1>(), determineParameterName(p1, pathParams, 0), p1, pathParams),
                    ParameterInfo(typeOf<P2>(), determineParameterName(p2, pathParams, 1), p2, pathParams)
                )
            )

            @Suppress("UNCHECKED_CAST")
            handler(params[0] as P1, params[1] as P2)
        } catch (e: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad Request")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${e.message}")
        }
    }
}

/**
 * FastAPI-style POST route with parameter metadata support.
 */
inline fun <reified P1> Route.post(
    path: String,
    p1: Input<P1>,
    crossinline handler: suspend RoutingContext.(P1) -> Unit
) = route(path, HttpMethod.Post) {
    handle {
        try {
            val pathParams = extractPathParameterNames(path)
            val params = resolveFastAPIParameters(
                listOf(ParameterInfo(typeOf<P1>(), determineParameterName(p1, pathParams, 0), p1, pathParams))
            )

            @Suppress("UNCHECKED_CAST")
            handler(params[0] as P1)
        } catch (e: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad Request")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${e.message}")
        }
    }
}

/**
 * FastAPI-style POST route with 3 parameters.
 */
inline fun <reified P1, reified P2, reified P3> Route.post(
    path: String,
    p1: Input<P1>,
    p2: Input<P2>,
    p3: Input<P3>,
    crossinline handler: suspend RoutingContext.(P1, P2, P3) -> Unit
) = route(path, HttpMethod.Post) {
    handle {
        try {
            val pathParams = extractPathParameterNames(path)
            val params = resolveFastAPIParameters(
                listOf(
                    ParameterInfo(typeOf<P1>(), determineParameterName(p1, pathParams, 0), p1, pathParams),
                    ParameterInfo(typeOf<P2>(), determineParameterName(p2, pathParams, 1), p2, pathParams),
                    ParameterInfo(typeOf<P3>(), determineParameterName(p3, pathParams, 2), p3, pathParams)
                )
            )

            @Suppress("UNCHECKED_CAST")
            handler(params[0] as P1, params[1] as P2, params[2] as P3)
        } catch (e: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad Request")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${e.message}")
        }
    }
}

/**
 * FastAPI-style POST route with 4 parameters.
 */
inline fun <reified P1, reified P2, reified P3, reified P4> Route.post(
    path: String,
    p1: Input<P1>,
    p2: Input<P2>,
    p3: Input<P3>,
    p4: Input<P4>,
    crossinline handler: suspend RoutingContext.(P1, P2, P3, P4) -> Unit
) = route(path, HttpMethod.Post) {
    handle {
        try {
            val pathParams = extractPathParameterNames(path)
            val params = resolveFastAPIParameters(
                listOf(
                    ParameterInfo(typeOf<P1>(), determineParameterName(p1, pathParams, 0), p1, pathParams),
                    ParameterInfo(typeOf<P2>(), determineParameterName(p2, pathParams, 1), p2, pathParams),
                    ParameterInfo(typeOf<P3>(), determineParameterName(p3, pathParams, 2), p3, pathParams),
                    ParameterInfo(typeOf<P4>(), determineParameterName(p4, pathParams, 3), p4, pathParams)
                )
            )

            @Suppress("UNCHECKED_CAST")
            handler(params[0] as P1, params[1] as P2, params[2] as P3, params[3] as P4)
        } catch (e: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad Request")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${e.message}")
        }
    }
}

/**
 * FastAPI-style GET route with 3 parameters.
 */
inline fun <reified P1, reified P2, reified P3> Route.get(
    path: String,
    p1: Input<P1>,
    p2: Input<P2>,
    p3: Input<P3>,
    crossinline handler: suspend RoutingContext.(P1, P2, P3) -> Unit
) = route(path, HttpMethod.Get) {
    handle {
        try {
            val pathParams = extractPathParameterNames(path)
            val params = resolveFastAPIParameters(
                listOf(
                    ParameterInfo(typeOf<P1>(), determineParameterName(p1, pathParams, 0), p1, pathParams),
                    ParameterInfo(typeOf<P2>(), determineParameterName(p2, pathParams, 1), p2, pathParams),
                    ParameterInfo(typeOf<P3>(), determineParameterName(p3, pathParams, 2), p3, pathParams)
                )
            )

            @Suppress("UNCHECKED_CAST")
            handler(params[0] as P1, params[1] as P2, params[2] as P3)
        } catch (e: BadRequestException) {
            call.respond(HttpStatusCode.BadRequest, e.message ?: "Bad Request")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error: ${e.message}")
        }
    }
}

/**
 * Represents information about a parameter including its type and metadata.
 */
@PublishedApi
internal data class ParameterInfo<T>(
    val type: KType,
    val name: String,
    val metadata: Input<T>,
    val pathParams: List<String>
)



/**
 * Extracts path parameter names from a route path.
 * E.g., "/items/{item_id}" -> ["item_id"]
 */
@PublishedApi
internal fun extractPathParameterNames(path: String): List<String> {
    val regex = Regex("\\{([^}]+)\\}")
    return regex.findAll(path).map { it.groupValues[1] }.toList()
}

/**
 * Determines the parameter name based on the input type and position.
 */
@PublishedApi
internal fun determineParameterName(input: Input<*>, pathParams: List<String>, index: Int): String {
    return when (input) {
        is Path<*> -> {
            // For path parameters, use the path parameter name at the given index
            if (index < pathParams.size) pathParams[index]
            else throw IllegalArgumentException("Path parameter at index $index not found in path parameters: $pathParams")
        }
        is Query<*> -> {
            // For query parameters, use explicit name or fallback to common names
            input.name ?: when (index) {
                0 -> "q"
                1 -> "filter"
                2 -> "sort"
                3 -> "limit"
                4 -> "offset"
                else -> "param$index"
            }
        }
        is Header<*> -> {
            // For header parameters, use explicit name or fallback to common names
            input.name ?: when (index) {
                0 -> "user_agent"
                1 -> "x_token"
                2 -> "authorization"
                3 -> "content_type"
                4 -> "accept"
                else -> "header$index"
            }
        }
        is Body<*> -> {
            // For body parameters, use a generic name
            "body"
        }
    }
}

/**
 * Resolves parameters using FastAPI-style metadata.
 */
@PublishedApi
internal suspend fun RoutingContext.resolveFastAPIParameters(paramInfos: List<ParameterInfo<*>>): List<Any?> {
    return paramInfos.map { paramInfo ->
        resolveFastAPIParameter(paramInfo)
    }
}

/**
 * Resolves a single parameter using FastAPI-style metadata.
 */
internal suspend fun RoutingContext.resolveFastAPIParameter(paramInfo: ParameterInfo<*>): Any? {
    val classifier = paramInfo.type.classifier as? KClass<*>
        ?: throw IllegalArgumentException("Unsupported parameter type: ${paramInfo.type}")
    val isNullable = paramInfo.type.isMarkedNullable

    return when (paramInfo.metadata) {
        // Body parameters - parse from request body
        is Body<*> -> {
            try {
                val typeInfo = TypeInfo(classifier, paramInfo.type)
                call.receive(typeInfo)
            } catch (e: ContentTransformationException) {
                if (isNullable) null
                else throw BadRequestException("Failed to parse request body for parameter '${paramInfo.name}'", e)
            } catch (e: Exception) {
                if (isNullable) null
                else throw BadRequestException("Failed to parse request body for parameter '${paramInfo.name}'", e)
            }
        }

        // Path, Query, Header parameters - resolve based on metadata
        is Path<*>, is Query<*>, is Header<*> -> {
            val value = when (paramInfo.metadata) {
                is Path<*> -> {
                    // Path parameters
                    val pathValue = call.parameters[paramInfo.name]
                    if (pathValue == null && paramInfo.metadata.default == null && !isNullable) {
                        throw BadRequestException("Missing required path parameter: ${paramInfo.name}")
                    }
                    pathValue ?: paramInfo.metadata.default?.toString()
                }

                is Query<*> -> {
                    // Query parameters
                    val queryValue = call.request.queryParameters[paramInfo.name]
                    if (queryValue == null && paramInfo.metadata.default == null && !isNullable) {
                        throw BadRequestException("Missing required query parameter: ${paramInfo.name}")
                    }
                    queryValue ?: paramInfo.metadata.default?.toString()
                }

                is Header<*> -> {
                    // Header parameters
                    val headerName = if (paramInfo.metadata.convertUnderscores) {
                        paramInfo.name.replace('_', '-')
                    } else {
                        paramInfo.name
                    }
                    val headerValue = call.request.headers[headerName]
                    if (headerValue == null && paramInfo.metadata.default == null && !isNullable) {
                        throw BadRequestException("Missing required header: $headerName")
                    }
                    headerValue ?: paramInfo.metadata.default?.toString()
                }

                else -> throw IllegalArgumentException("Unsupported parameter metadata: ${paramInfo.metadata}")
            }

            if (value == null) {
                if (isNullable) null
                else throw BadRequestException("Missing required parameter: ${paramInfo.name}")
            } else {
                val convertedValue = convertStringToType(value, classifier, paramInfo.name)

                // Apply validation
                validateParameter(convertedValue, paramInfo.metadata, paramInfo.name)
            }
        }


    }
}

/**
 * Determines if a type is complex (should be parsed from request body).
 */
internal fun isComplexType(kClass: KClass<*>): Boolean {
    return when (kClass) {
        String::class, Int::class, Long::class, Double::class, Float::class,
        Boolean::class, Byte::class, Char::class, Short::class -> false
        else -> true
    }
}

/**
 * Converts a string value to the target type.
 */
internal fun convertStringToType(value: String, targetClass: KClass<*>, paramName: String): Any {
    return try {
        when (targetClass) {
            String::class -> value
            Int::class -> value.toInt()
            Long::class -> value.toLong()
            Double::class -> value.toDouble()
            Float::class -> value.toFloat()
            Boolean::class -> value.toBooleanStrict()
            Byte::class -> value.toByte()
            Char::class -> value.single()
            Short::class -> value.toShort()
            else -> throw IllegalArgumentException("Unsupported parameter type: ${targetClass.simpleName}")
        }
    } catch (e: Exception) {
        throw BadRequestException("Invalid value '$value' for parameter '$paramName' of type ${targetClass.simpleName}", e)
    }
}