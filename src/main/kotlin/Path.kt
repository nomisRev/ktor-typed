@file:Suppress("UNCHECKED_CAST")

package com.example

import com.example.codec.Codec
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments

fun <A> Path(block: PathBuilder<Unit>.() -> PathBuilder<A>): Route<A, Unit> =
    Route(PathBuilder<Unit>().block(), emptyList(), { it }) { it }

class PathBuilder<A> {
    val segments = mutableListOf<Pair<String, Parameter.Path<*>?>>()
    var arity: Int = 0
    var transform: ((Any?) -> Any?)? = null

    fun addSegment(segment: String) {
        segments += segment to null
    }

    fun addSegment(segment: Parameter.Path<*>) {
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

    inline fun <reified B> PathBuilder<A>.jsonParam(name: String, optional: Boolean = false): Parameter<B> =
        TODO()
//        Parameter.Path(name, optional) { value -> Json.decodeFromString(serializer<B>(), value) }

    fun string(name: String) = Parameter.Path(name, Codec.string)
//    fun char(name: String) = Parameter.Path(name, false)  {
//        if (it.length == 1) it[0] else throw IllegalArgumentException("Expected a single character")
//    }

    fun int(name: String) = Parameter.Path(name, Codec.int)
    fun long(name: String) = Parameter.Path(name, Codec.long)
    fun double(name: String) = Parameter.Path(name, Codec.double)

    fun routeString(): String =
        segments.joinToString("/") { (segment, parameter) ->
            // TODO support nullable, but it can only be the last segment
//            val nullable = if (parameter?.optional == true) "?" else ""
            if (parameter == null) segment else "{${parameter.name}}"
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
operator fun <B> String.div(path: Parameter.Path<B>): PathBuilder<Params1<B>> {
    builder.addSegment(this)
    builder.addSegment(path)
    builder.arity++
    return builder as PathBuilder<Params1<B>>
}

@JvmName("stringDivPathParameter")
operator fun <B> PathBuilder<Params0>.div(path: Parameter.Path<B>): PathBuilder<Params1<B>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params1<B>>
}

operator fun <A> PathBuilder<A>.div(path: String): PathBuilder<A> {
    addSegment(path)
    return this as PathBuilder<A>
}

operator fun <A, B> PathBuilder<A>.div(path: Parameter.Path<B>): PathBuilder<Params2<A, B>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params2<A, B>>
}

@JvmName("params1DivPathParameter")
operator fun <A, B> PathBuilder<Params1<A>>.div(path: Parameter.Path<B>): PathBuilder<Params2<A, B>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params2<A, B>>
}

@JvmName("params2DivPathParameter")
operator fun <A, B, C> PathBuilder<Params2<A, B>>.div(path: Parameter.Path<C>): PathBuilder<Params3<A, B, C>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params3<A, B, C>>
}

@JvmName("params3DivPathParameter")
operator fun <A, B, C, D> PathBuilder<Params3<A, B, C>>.div(path: Parameter.Path<D>): PathBuilder<Params4<A, B, C, D>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params4<A, B, C, D>>
}

@JvmName("params4DivPathParameter")
operator fun <A, B, C, D, E> PathBuilder<Params4<A, B, C, D>>.div(path: Parameter.Path<E>): PathBuilder<Params5<A, B, C, D, E>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params5<A, B, C, D, E>>
}

@JvmName("params5DivPathParameter")
operator fun <A, B, C, D, E, F> PathBuilder<Params5<A, B, C, D, E>>.div(path: Parameter.Path<F>): PathBuilder<Params6<A, B, C, D, E, F>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params6<A, B, C, D, E, F>>
}

@JvmName("params6DivPathParameter")
operator fun <A, B, C, D, E, F, G> PathBuilder<Params6<A, B, C, D, E, F>>.div(path: Parameter.Path<G>): PathBuilder<Params7<A, B, C, D, E, F, G>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params7<A, B, C, D, E, F, G>>
}

@JvmName("params7DivPathParameter")
operator fun <A, B, C, D, E, F, G, H> PathBuilder<Params7<A, B, C, D, E, F, G>>.div(path: Parameter.Path<H>): PathBuilder<Params8<A, B, C, D, E, F, G, H>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params8<A, B, C, D, E, F, G, H>>
}

@JvmName("params8DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I> PathBuilder<Params8<A, B, C, D, E, F, G, H>>.div(path: Parameter.Path<I>): PathBuilder<Params9<A, B, C, D, E, F, G, H, I>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params9<A, B, C, D, E, F, G, H, I>>
}

@JvmName("params9DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J> PathBuilder<Params9<A, B, C, D, E, F, G, H, I>>.div(path: Parameter.Path<J>): PathBuilder<Params10<A, B, C, D, E, F, G, H, I, J>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params10<A, B, C, D, E, F, G, H, I, J>>
}


@JvmName("params10DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K> PathBuilder<Params10<A, B, C, D, E, F, G, H, I, J>>.div(path: Parameter.Path<K>): PathBuilder<Params11<A, B, C, D, E, F, G, H, I, J, K>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params11<A, B, C, D, E, F, G, H, I, J, K>>
}

@JvmName("params11DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L> PathBuilder<Params11<A, B, C, D, E, F, G, H, I, J, K>>.div(path: Parameter.Path<L>): PathBuilder<Params12<A, B, C, D, E, F, G, H, I, J, K, L>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params12<A, B, C, D, E, F, G, H, I, J, K, L>>
}

@JvmName("params12DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M> PathBuilder<Params12<A, B, C, D, E, F, G, H, I, J, K, L>>.div(path: Parameter.Path<M>): PathBuilder<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>>
}

@JvmName("params13DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> PathBuilder<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>>.div(
    path: Parameter.Path<N>
): PathBuilder<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>>
}

@JvmName("params14DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> PathBuilder<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>>.div(
    path: Parameter.Path<O>
): PathBuilder<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>>
}

@JvmName("params15DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> PathBuilder<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>>.div(
    path: Parameter.Path<P>
): PathBuilder<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>>
}

@JvmName("params16DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> PathBuilder<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>>.div(
    path: Parameter.Path<Q>
): PathBuilder<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>>
}

@JvmName("params17DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> PathBuilder<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>>.div(
    path: Parameter.Path<R>
): PathBuilder<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>>
}

@JvmName("params18DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> PathBuilder<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>>.div(
    path: Parameter.Path<S>
): PathBuilder<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>>
}

@JvmName("params19DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> PathBuilder<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>>.div(
    path: Parameter.Path<T>
): PathBuilder<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>>
}

@JvmName("params20DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> PathBuilder<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>>.div(
    path: Parameter.Path<U>
): PathBuilder<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>>
}

@JvmName("params21DivPathParameter")
operator fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> PathBuilder<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>>.div(
    path: Parameter.Path<V>
): PathBuilder<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>> {
    addSegment(path)
    arity++
    return this as PathBuilder<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>>
}
