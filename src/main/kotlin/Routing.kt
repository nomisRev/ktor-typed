package com.example

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

context(routing: io.ktor.server.routing.Route)
internal fun <Input, Output> Route<Input, Output>.handle(
    method: HttpMethod,
    block: suspend RoutingContext.(Input) -> Unit
) =
    routing.route(path.routeString(), method) {
        handle {
            val params = (path.segments
                .mapNotNull { path ->
                    if (path.second == null) null
                    else path as Pair<String, Parameter.Path<*>>
                }
                .map { (segment, parameter) ->
                    val value = call.parameters[segment]
                    if (parameter.isNullable && value == null) null
                    else parameter.codec.deserialize(requireNotNull(value) { "Parameter ${parameter.name} is required but was null." })
                } + parameters.map { parameter ->
                when (parameter) {
                    is Parameter.Cookie -> call.request.cookies[parameter.name, parameter.encoding]
                    is Parameter.Header -> call.request.headers[parameter.name]
//                    is Parameter.Headers -> call.request.headers.getAll(parameter.name)
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
            val value = when (params) {
                is Params0 -> Unit
                is Params1<*> -> params.value
                else -> params
            }
            block(transform(value))
        }
    }