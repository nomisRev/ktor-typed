package com.example

import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.server.routing.Routing
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import kotlin.text.toInt

class Route<Input, Output>(
    val method: HttpMethod,
    val path: PathBuilder<*>,
    val query: QueryParameter<*>?,
    var transform: (suspend (Any?) -> Any?)? = null,
) {
    context(routing: Routing)
    fun handle(block: suspend RoutingContext.(Input) -> Unit) =
        routing.route(path.routeString(), method) {
            handle {
                val params = path.segments.mapNotNull { (_, parameter) ->
                    when {
                        parameter == null -> null
                        parameter.optional ->
                            call.parameters[parameter.name]?.let { parameter.deserialize(it) }

                        else -> parameter.deserialize(call.parameters[parameter.name]!!)
                    }
                }

                // TODO transform path
//                val path = (path.transform?.let { it(value) } ?: value) as Input

                val deserialized = if (query != null) {
                    val value = call.request.queryParameters[query.name]
                    // TODO implement optional
                    query.deserialize(value!!)
                } else null

                val value = Triple(
                    params[0],
                    params[1],
                    deserialized
                )

                val transformed = transform?.let { it(value) } ?: value

                block(transformed as Input)
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
}

fun <Input, A> Route<Input, Unit>.query(
    param: QueryParameter<A>
): Route<Pair<Input, A>, Unit> =
    Route(method = method, path = path, query = param)

@JvmName("queryPair")
fun <A, B, C> Route<Pair<A, B>, Unit>.query(
    param: QueryParameter<C>
): Route<Triple<A, B, C>, Unit> =
    Route(method = method, path = path, query = param)


class QueryParameter<A>(
    val name: String,
    val deserialize: suspend (String) -> A,
    val optional: Boolean,
)

class PathParameter<A>(
    val name: String,
    val deserialize: suspend (String) -> A,
    val optional: Boolean,
)

class PathBuilder<A> {
    val segments = mutableListOf<Pair<String, PathParameter<*>?>>()
    var arity: Int = 0
    var transform: ((Any?) -> Any?)? = null

    fun addSegment(segment: String) {
        segments += segment to null
    }

    fun addSegment(segment: PathParameter<*>) {
        segments += segment.name to segment
    }

    operator fun String.div(other: String): PathBuilder<A> {
        addSegment(this)
        addSegment(other)
        return this@PathBuilder
    }

    operator fun PathBuilder<A>.div(other: String): PathBuilder<A> = also {
        addSegment(other)
    }

    inline fun <reified B> PathBuilder<A>.param(name: String, optional: Boolean = false): PathParameter<B> =
        PathParameter(name, { TODO() }, optional)

    fun string(name: String) = PathParameter(name, { it }, false)
    fun char(name: String) = PathParameter(name, {
        if (it.length == 1) it[0] else throw IllegalArgumentException("Expected a single character")
    }, false)

    fun int(name: String) = PathParameter(name, { it.toInt() }, false)
//    fun long(name: String) = PathParameter(name, Long.serializer(), false)
//    fun double(name: String) = PathParameter(name, Double.serializer(), false)

//    fun optString(name: String) = PathParameter(name, String.serializer(), true)
//    fun optChar(name: String) = PathParameter(name, Char.serializer(), true)
//    fun optInt(name: String) = PathParameter(name, Int.serializer(), true)
//    fun optLong(name: String) = PathParameter(name, Long.serializer(), true)
//    fun optDouble(name: String) = PathParameter(name, Double.serializer(), true)

    fun routeString(): String =
        segments.joinToString("/") { (segment, parameter) ->
            val nullable = if (parameter?.optional == true) "?" else ""
            if (parameter == null) segment
            else "{${parameter.name}$nullable}"
        }

    fun builder() = URLBuilder().apply {
        appendPathSegments(segments.map { it.first })
    }

    fun <B> map(block: (A) -> B): PathBuilder<B> {
        val original = transform
        if (original == null) {
            transform = block as (Any?) -> Any?
        } else {
            transform = {
                val a = original(it) as A
                block(a) as Any?
            }
        }
        return this as PathBuilder<B>
    }
}

context(builder: PathBuilder<Unit>)
operator fun <B> String.div(path: PathParameter<B>): PathBuilder<B> {
    builder.addSegment(this)
    builder.addSegment(path)
    builder.arity++
    return builder as PathBuilder<B>
}

@JvmName("stringDivPathParameter")
operator fun <B> PathBuilder<Unit>.div(path: PathParameter<B>): PathBuilder<B> {
    addSegment(path)
    arity++
    return this as PathBuilder<B>
}

operator fun <A> PathBuilder<A>.div(path: String): PathBuilder<A> {
    addSegment(path)
    return this as PathBuilder<A>
}

operator fun <A, B> PathBuilder<A>.div(path: PathParameter<B>): PathBuilder<Pair<A, B>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Pair<A, B>>
}

fun <A> Path(block: PathBuilder<Unit>.() -> PathBuilder<A>): PathBuilder<A> =
    PathBuilder<Unit>().block()

fun <A> PathBuilder<A>.get(): Route<A, Unit> = Route(
    HttpMethod.Get,
    this,
    null,
)
