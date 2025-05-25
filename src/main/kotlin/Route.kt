package com.example

import io.ktor.http.Cookie
import io.ktor.http.HttpMethod
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route

fun <Input> Routing.get(route: Route<Input, Unit>, block: suspend RoutingContext.(Input) -> Unit) =
    route.handle(HttpMethod.Get, block)

fun <Input> Routing.post(route: Route<Input, Unit>, block: suspend RoutingContext.(Input) -> Unit) =
    route.handle(HttpMethod.Post, block)

fun <Input> Routing.put(route: Route<Input, Unit>, block: suspend RoutingContext.(Input) -> Unit) =
    route.handle(HttpMethod.Put, block)

fun <Input> Routing.patch(route: Route<Input, Unit>, block: suspend RoutingContext.(Input) -> Unit) =
    route.handle(HttpMethod.Patch, block)

fun <Input> Routing.delete(route: Route<Input, Unit>, block: suspend RoutingContext.(Input) -> Unit) =
    route.handle(HttpMethod.Delete, block)

fun <Input> Routing.head(route: Route<Input, Unit>, block: suspend RoutingContext.(Input) -> Unit) =
    route.handle(HttpMethod.Head, block)

fun <Input> Routing.options(route: Route<Input, Unit>, block: suspend RoutingContext.(Input) -> Unit) =
    route.handle(HttpMethod.Options, block)


class Route<Input, Output>(
    private val path: PathBuilder<*>,
    private var transform: (suspend (Any?) -> Any?)? = null,
) {
    private val queryParameters: MutableList<Parameter.Query<*>> = mutableListOf()
    private val headerParameters: MutableList<Parameter.Header<*>> = mutableListOf()
    private val cookieParameters: MutableList<Parameter.Cookie<*>> = mutableListOf()

    context(routing: Routing)
    internal fun handle(method: HttpMethod, block: suspend RoutingContext.(Input) -> Unit) =
        routing.route(path.routeString().also { println(it) }, method) {
            handle {
                buildList {
                    path.segments.forEach { (_, parameter) ->
                        when {
                            parameter == null -> null
                            parameter.optional -> add(call.pathParameters[parameter.name]
                                ?.let { parameter.deserialize(it) })

                            else -> add(parameter.deserialize(call.pathParameters[parameter.name]!!))
                        }
                    }

                    queryParameters.forEach { query ->
                        val value = call.request.queryParameters[query.name]
                        // TODO implement optional
                        add(query.deserialize(value!!))
                    }

                    headerParameters.forEach { header ->
                        val value = call.request.headers[header.name]
                        // TODO implement optional
                        add(header.deserialize(value!!))
                    }

                    cookieParameters.forEach { cookie ->
                        // Get cookie value from the request
                        val value = call.request.headers["Cookie"]
                        // TODO implement optional
                        add(cookie.deserialize(value!!))
                    }
                }.execute(block as suspend RoutingContext.(Any?) -> Unit, transform)
            }
        }


    fun <B> map(block: suspend (Input) -> B): Route<B, Output> {
        val original = transform
        if (original == null) {
            transform = block as suspend (Any?) -> Any?
        } else {
            transform = {
                val a = original(it) as Input
                block(a) as Any?
            }
        }
        return this as Route<B, Output>
    }

    internal fun addQuery(query: Parameter.Query<*>) =
        also { queryParameters.add(query) }

    internal fun addHeader(header: Parameter.Header<*>) =
        also { headerParameters.add(header) }

    internal fun addCookie(cookie: Parameter.Cookie<*>) =
        also { cookieParameters.add(cookie) }
}
