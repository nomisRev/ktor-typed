package com.example

import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.serializer

data class Route<Path, Output>(
    val method: HttpMethod,
    val path: PathBuilder<*>,
)

class PathParameter<A>(
    val name: String,
    val serializer: KSerializer<A>,
    val optional: Boolean,
)


class PathBuilder<A> {
    private val segments = mutableListOf<Pair<String, PathParameter<*>?>>()
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
        PathParameter(name, serializer<B>(), optional)

    fun string(name: String) = PathParameter(name, String.serializer(), false)
    fun char(name: String) = PathParameter(name, Char.serializer(), false)
    fun int(name: String) = PathParameter(name, Int.serializer(), false)
    fun long(name: String) = PathParameter(name, Long.serializer(), false)
    fun double(name: String) = PathParameter(name, Double.serializer(), false)

    fun optString(name: String) = PathParameter(name, String.serializer(), true)
    fun optChar(name: String) = PathParameter(name, Char.serializer(), true)
    fun optInt(name: String) = PathParameter(name, Int.serializer(), true)
    fun optLong(name: String) = PathParameter(name, Long.serializer(), true)
    fun optDouble(name: String) = PathParameter(name, Double.serializer(), true)

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
