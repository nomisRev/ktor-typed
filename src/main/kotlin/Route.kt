package com.example

import io.ktor.http.Cookie
import io.ktor.http.HttpMethod
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route

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
    private var transform: (suspend (Any?) -> Any?)? = null,
) {
    val parameters = mutableListOf<Parameter<*>>()
    val arity get() = path.arity + parameters.size

    // TODO: Split this to a server module. This module can be build on io.ktor.http
    context(routing: io.ktor.server.routing.Route)
    internal fun handle(method: HttpMethod, block: suspend RoutingContext.(Input) -> Unit) =
        routing.route(path.routeString(), method) {
            handle {
                (path.segments.mapNotNull { (segment, parameter) ->
                    if (parameter != null) {
                        val value = call.parameters[segment]
                        parameter.deserialize.deserialize(requireNotNull(value))
                    } else null
                } + parameters.map { parameter ->
                    when (parameter) {
                        is Parameter.Cookie -> call.request.cookies[parameter.name, parameter.encoding]
                        is Parameter.Header -> call.request.headers.getAll(parameter.name)
                        is Parameter.Query<*> -> {
                            val value = call.request.queryParameters[parameter.name]
                            when {
                                value == null -> if (parameter.optional) null else throw IllegalStateException("Parameter ${parameter.name} is required")
                                else -> parameter.deserialize.deserialize(value)
                            }
                        }
                    }
                }).execute(block as suspend RoutingContext.(Any?) -> Unit, transform)
            }
        }

    internal fun addQuery(query: Parameter.Query<*>) =
        also { parameters.add(query) }

    internal fun addHeader(header: Parameter.Header) =
        also { parameters.add(header) }

    internal fun addCookie(cookie: Parameter.Cookie) =
        also { parameters.add(cookie) }
}
