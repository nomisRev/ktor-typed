package com.example

import io.ktor.http.HttpMethod
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import kotlin.collections.emptyList

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
                .mapNotNull { path -> if (path.second == null) null else path as Pair<String, Path<*>> }
                .map { (_, parameter) ->
                    when(parameter) {
                        is Path.Required<*> -> parameter.deserialize(call.parameters.getOrFail(parameter.name))
                        is Path.Optional<*> -> {
                            val optional = call.parameters[parameter.name]
                            if (optional == null) parameter.defaultValue
                            else parameter.deserialize(optional)
                        }
                        is Path.Multiple<*> -> {
                            val values = (call.parameters.getAll(parameter.name) ?: emptyList<String>())
                            parameter.deserialize(values)
                        }
                    }
                } + parameters.map { parameter ->
                when (parameter) {

                    is Parameter.Cookie -> call.request.cookies[parameter.name/*, parameter.encoding*/] // TODO
                    is Parameter.Header -> call.request.headers[parameter.name]
//                    is Parameter.Headers -> call.request.headers.getAll(parameter.name)
                    is Parameter.Query<*> -> {
                        when (parameter) {
                            is Parameter.Query.Required<*> -> parameter.deserialize(call.request.queryParameters.getOrFail(parameter.name))
                            is Parameter.Query.Optional<*> -> {
                                val optional = call.request.queryParameters[parameter.name]
                                if (optional == null) parameter.defaultValue
                                else parameter.deserialize(optional)
                            }
                            is Parameter.Query.Multiple<*> -> {
                                val values = (call.request.queryParameters.getAll(parameter.name) ?: emptyList<String>())
                                parameter.deserialize(values)
                            }
                            is Parameter.Query.AllParameters -> call.request.queryParameters
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
