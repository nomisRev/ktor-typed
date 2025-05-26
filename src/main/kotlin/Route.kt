package com.example

import io.ktor.http.HttpMethod
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import kotlin.collections.map

fun <Input> io.ktor.server.routing.Route.get(route: Route<Input, Unit>, block: suspend RoutingContext.(Input) -> Unit) =
    route.handle(HttpMethod.Get, block)

fun <Input> io.ktor.server.routing.Route.post(
    route: Route<Input, Unit>,
    block: suspend RoutingContext.(Input) -> Unit
) =
    route.handle(HttpMethod.Post, block)

fun <Input> io.ktor.server.routing.Route.put(route: Route<Input, Unit>, block: suspend RoutingContext.(Input) -> Unit) =
    route.handle(HttpMethod.Put, block)

fun <Input> io.ktor.server.routing.Route.patch(
    route: Route<Input, Unit>,
    block: suspend RoutingContext.(Input) -> Unit
) =
    route.handle(HttpMethod.Patch, block)

fun <Input> io.ktor.server.routing.Route.delete(
    route: Route<Input, Unit>,
    block: suspend RoutingContext.(Input) -> Unit
) =
    route.handle(HttpMethod.Delete, block)

fun <Input> io.ktor.server.routing.Route.head(
    route: Route<Input, Unit>,
    block: suspend RoutingContext.(Input) -> Unit
) =
    route.handle(HttpMethod.Head, block)

fun <Input> io.ktor.server.routing.Route.options(
    route: Route<Input, Unit>,
    block: suspend RoutingContext.(Input) -> Unit
) =
    route.handle(HttpMethod.Options, block)

class Route<Input, Output>(
    val path: PathBuilder<*>,
    val parameters: List<Parameter<*>>,
    val transform: suspend (Any?) -> Any?,
    val reverse: suspend (Any?) -> Any?
) {
    val arity get() = path.arity + parameters.size

    internal fun addQuery(query: Parameter.Query<*>): Route<Input, Output> = Route(
        path,
        parameters + query,
        transform,
        reverse
    )

    internal fun addHeader(header: Parameter.Header): Route<Input, Output> = Route(
        path,
        parameters + header,
        transform,
        reverse
    )

    internal fun addCookie(cookie: Parameter.Cookie): Route<Input, Output> = Route(
        path,
        parameters + cookie,
        transform,
        reverse
    )
}

context(routing: io.ktor.server.routing.Route)
internal fun <Input, Output> Route<Input, Output>.handle(
    method: HttpMethod,
    block: suspend RoutingContext.(Input) -> Unit
) =
    routing.route(path.routeString(), method) {
        handle {
            val params = (path.segments.mapNotNull { (segment, parameter) ->
                if (parameter != null) {
                    val value = call.parameters[segment]
                    parameter.codec.deserialize(requireNotNull(value))
                } else null
            } + parameters.map { parameter ->
                when (parameter) {
                    is Parameter.Cookie -> call.request.cookies[parameter.name, parameter.encoding]
                    is Parameter.Header -> call.request.headers.getAll(parameter.name)
                    is Parameter.Query<*> -> {
                        val value = call.request.queryParameters[parameter.name]
                        when {
                            value == null -> if (parameter.isNullable) null else throw IllegalStateException("Parameter ${parameter.name} is required")
                            else -> parameter.codec.deserialize(value)
                        }
                    }
                }
            }).toParams()
            block as suspend RoutingContext.(Any?) -> Unit
            block(transform(params))
        }
    }
