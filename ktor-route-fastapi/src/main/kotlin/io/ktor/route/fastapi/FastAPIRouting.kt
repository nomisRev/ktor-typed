package io.ktor.route.fastapi

import io.ktor.http.HttpMethod
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.request.receive
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
 *     put("/items/{item_id}") {
 *         itemId: Int,
 *         q: String?,
 *         item: Item,
 *         userAgent: String?,
 *         xToken: String? ->
 *
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
    p1: Any = DefaultParam,
    p2: Any = DefaultParam,
    p3: Any = DefaultParam,
    p4: Any = DefaultParam,
    p5: Any = DefaultParam,
    crossinline handler: suspend RoutingContext.(P1, P2, P3, P4, P5) -> Unit
) = route(path, HttpMethod.Put) {
    handle {
        val pathParams = extractPathParameterNames(path)
        val params = resolveFastAPIParameters(
            listOf(
                ParameterInfo(typeOf<P1>(), "item_id", p1, pathParams),
                ParameterInfo(typeOf<P2>(), "q", p2, pathParams),
                ParameterInfo(typeOf<P3>(), "item", p3, pathParams),
                ParameterInfo(typeOf<P4>(), "user_agent", p4, pathParams),
                ParameterInfo(typeOf<P5>(), "x_token", p5, pathParams)
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
    }
}

/**
 * FastAPI-style GET route with parameter metadata support.
 */
inline fun <reified P1, reified P2> Route.get(
    path: String,
    p1: Any = DefaultParam,
    p2: Any = DefaultParam,
    crossinline handler: suspend RoutingContext.(P1, P2) -> Unit
) = route(path, HttpMethod.Get) {
    handle {
        val pathParams = extractPathParameterNames(path)
        val params = resolveFastAPIParameters(
            listOf(
                ParameterInfo(typeOf<P1>(), "name", p1, pathParams),
                ParameterInfo(typeOf<P2>(), "age", p2, pathParams)
            )
        )

        @Suppress("UNCHECKED_CAST")
        handler(params[0] as P1, params[1] as P2)
    }
}

/**
 * FastAPI-style POST route with parameter metadata support.
 */
inline fun <reified P1> Route.post(
    path: String,
    p1: Any = DefaultParam,
    crossinline handler: suspend RoutingContext.(P1) -> Unit
) = route(path, HttpMethod.Post) {
    handle {
        val pathParams = extractPathParameterNames(path)
        val params = resolveFastAPIParameters(
            listOf(ParameterInfo(typeOf<P1>(), "request", p1, pathParams))
        )

        @Suppress("UNCHECKED_CAST")
        handler(params[0] as P1)
    }
}

/**
 * Represents information about a parameter including its type and metadata.
 */
@PublishedApi
internal data class ParameterInfo(
    val type: KType,
    val name: String,
    val metadata: Any,
    val pathParams: List<String>
)

/**
 * Sentinel object to represent default parameter metadata.
 */
object DefaultParam

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
 * Resolves parameters using FastAPI-style metadata.
 */
@PublishedApi
internal suspend fun RoutingContext.resolveFastAPIParameters(paramInfos: List<ParameterInfo>): List<Any?> {
    return paramInfos.map { paramInfo ->
        resolveFastAPIParameter(paramInfo)
    }
}

/**
 * Resolves a single parameter using FastAPI-style metadata.
 */
internal suspend fun RoutingContext.resolveFastAPIParameter(paramInfo: ParameterInfo): Any? {
    val classifier = paramInfo.type.classifier as? KClass<*>
        ?: throw IllegalArgumentException("Unsupported parameter type: ${paramInfo.type}")
    val isNullable = paramInfo.type.isMarkedNullable

    return when {
        // Complex types (data classes) - parse from request body
        isComplexType(classifier) -> {
            try {
                val typeInfo = TypeInfo(classifier, paramInfo.type)
                if (isNullable) {
                    call.receiveNullable<Any?>(typeInfo)
                } else {
                    call.receive<Any>(typeInfo)
                }
            } catch (e: ContentTransformationException) {
                if (isNullable) null
                else throw BadRequestException("Failed to parse request body for parameter '${paramInfo.name}'", e)
            }
        }

        // Primitive types - resolve based on metadata
        else -> {
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

                DefaultParam -> {
                    // Auto-detect parameter source
                    when {
                        paramInfo.name in paramInfo.pathParams -> {
                            // It's a path parameter
                            val pathValue = call.parameters[paramInfo.name]
                            if (pathValue == null && !isNullable) {
                                throw BadRequestException("Missing required path parameter: ${paramInfo.name}")
                            }
                            pathValue
                        }
                        else -> {
                            // Try query parameter first, then header
                            call.request.queryParameters[paramInfo.name]
                                ?: call.request.headers[paramInfo.name.replace('_', '-')]
                        }
                    }
                }

                else -> throw IllegalArgumentException("Unsupported parameter metadata: ${paramInfo.metadata}")
            }

            if (value == null) {
                if (isNullable) null
                else throw BadRequestException("Missing required parameter: ${paramInfo.name}")
            } else {
                val convertedValue = convertStringToType(value, classifier, paramInfo.name)

                // Apply validation if metadata is provided
                if (paramInfo.metadata != DefaultParam) {
                    validateParameter(convertedValue, paramInfo.metadata, paramInfo.name)
                } else {
                    convertedValue
                }
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
            Boolean::class -> value.toBoolean()
            Byte::class -> value.toByte()
            Char::class -> value.single()
            Short::class -> value.toShort()
            else -> throw IllegalArgumentException("Unsupported parameter type: ${targetClass.simpleName}")
        }
    } catch (e: Exception) {
        throw BadRequestException("Invalid value '$value' for parameter '$paramName' of type ${targetClass.simpleName}", e)
    }
}