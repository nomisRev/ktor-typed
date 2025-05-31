@file:Suppress("UNCHECKED_CAST")

package com.example

import com.example.PathResult.PartialPath
import com.example.PathResult.PathDone
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

fun pathOf(simple: String): Route<Unit, Unit> = Route(
    PartialPath<Unit>(listOf(Pair(simple, null)), 0),
    emptyList(),
    { it }
) { it }

/**
 * Path DSL for defining Path's in a typed manner.
 * This prevents creating incorrect paths by mistake, and automatically keeps track of all types required by a [Route].
 * For example, optional path parameters are enforced to only be available the last parameter.
 *
 * Let's see an example for: `/products/{productId}/reviews/{username?}`
 *
 * ```kotlin
 * val route: Route<Params2<Long, String?>> =
 *   Path { "products" / long("productId") / "reviews" / stringOrNull("username") }
 * ```
 */
fun <A> Path(block: PathSyntax.() -> PathResult<A>): Route<A, Unit> =
    Route(PathSyntax.block(), emptyList(), { it }) { it }

/**
 * Result of the [Path] DSL builder.
 * It's either a [PartialPath] which can be composed further,
 * or [PathDone] which means a final parameter has been composed such as an optional path parameter.
 */
sealed interface PathResult<A> {
    val segments: List<Pair<String, Path<*>?>>
    var arity: Int

    data class PathDone<A>(
        override val segments: List<Pair<String, Path<*>?>>,
        override var arity: Int
    ) : PathResult<A>

    data class PartialPath<A>(
        override val segments: List<Pair<String, Path<*>?>>,
        override var arity: Int
    ) : PathResult<A>

    fun routeString(): String =
        segments.joinToString("/") { (segment, parameter) ->
            when (parameter) {
                is Path.Required<*> -> "{${parameter.name}}"
                is Path.Optional<*> -> "{${parameter.name}?}"
                is Path.Multiple<*> -> "{${parameter.name}...}"
                null -> segment
            }
        }
}

/* Syntatic sugar for within the Path DSL. */
object PathSyntax {
    fun string(name: String): Path.Required<String> =
        Path.Required(name, { it }) { it }

    fun stringOrNull(name: String): Path.Optional<String?> =
        Path.Optional(name, null, { it }) { it }

    fun bool(name: String): Path.Required<Boolean> =
        Path.Required(name, { it.toBooleanStrict() }) { it.toString() }

    fun boolOrNull(name: String): Path.Optional<Boolean?> =
        Path.Optional(name, null, { it?.toBooleanStrict() }) { it?.toString() }

    fun byte(name: String): Path.Required<Byte> =
        Path.Required(name, { it.toByte() }) { it.toString() }

    fun byteOrNull(name: String): Path.Optional<Byte?> =
        Path.Optional(name, null, { it?.toByte() }) { it?.toString() }

    fun short(name: String): Path.Required<Short> =
        Path.Required(name, { it.toShort() }) { it.toString() }

    fun shortOrNull(name: String): Path.Optional<Short?> =
        Path.Optional(name, null, { it?.toShort() }) { it?.toString() }

    fun int(name: String): Path.Required<Int> =
        Path.Required(name, { it.toInt() }) { it.toString() }

    fun intOrNull(name: String): Path.Optional<Int?> =
        Path.Optional(name, null, { it?.toInt() }) { it?.toString() }

    fun long(name: String): Path.Required<Long> =
        Path.Required(name, { it.toLong() }) { it.toString() }

    fun longOrNull(name: String): Path.Optional<Long?> =
        Path.Optional(name, null, { it?.toLong() }) { it?.toString() }

    fun float(name: String): Path.Required<Float> =
        Path.Required(name, { it.toFloat() }) { it.toString() }

    fun floatOrNull(name: String): Path.Optional<Float?> =
        Path.Optional(name, null, { it?.toFloat() }) { it?.toString() }

    fun double(name: String): Path.Required<Double> =
        Path.Required(name, { it.toDouble() }) { it.toString() }

    fun doubleOrNull(name: String): Path.Optional<Double?> =
        Path.Optional(name, null, { it?.toDouble() }) { it?.toString() }
}

// <editor-fold desc="Path DSL overloads for composing types on the typelevel">

context(syntax: PathSyntax)
operator fun <B : Any> String.div(path: Path<B>): PartialPath<B> =
    PartialPath(listOf(Pair(this, null), Pair(path.name, path)), arity = 1)

context(syntax: PathSyntax)
operator fun <B> String.div(path: Path<B?>): PathDone<B> =
    PathDone(listOf(Pair(this, null), Pair(path.name, path)), arity = 1)

operator fun <A : Any> PartialPath<A>.div(path: String): PartialPath<A> =
    PartialPath(segments + Pair(path, null), arity = arity)

operator fun <A : Any, B : Any> PartialPath<A>.div(path: Path<B>): PartialPath<Params2<A, B>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

operator fun <A : Any, B> PartialPath<A>.div(path: Path<B?>): PathDone<Params2<A, B?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params2DivPathParameter")
operator fun <A : Any, B : Any, C : Any> PartialPath<Params2<A, B>>.div(path: Path<C>): PartialPath<Params3<A, B, C>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params2DivPathParameter")
operator fun <A : Any, B : Any, C> PartialPath<Params2<A, B>>.div(path: Path<C?>): PathDone<Params3<A, B, C?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)


@JvmName("params3DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any> PartialPath<Params3<A, B, C>>.div(path: Path<D>): PartialPath<Params4<A, B, C, D>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params3DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D> PartialPath<Params3<A, B, C>>.div(path: Path<D?>): PathDone<Params4<A, B, C, D?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params4DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any> PartialPath<Params4<A, B, C, D>>.div(path: Path<E>): PartialPath<Params5<A, B, C, D, E>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params4DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E> PartialPath<Params4<A, B, C, D>>.div(path: Path<E?>): PathDone<Params5<A, B, C, D, E?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params5DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any> PartialPath<Params5<A, B, C, D, E>>.div(path: Path<F>): PartialPath<Params6<A, B, C, D, E, F>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params5DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F> PartialPath<Params5<A, B, C, D, E>>.div(path: Path<F?>): PathDone<Params6<A, B, C, D, E, F?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params6DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any> PartialPath<Params6<A, B, C, D, E, F>>.div(
    path: Path<G>
): PartialPath<Params7<A, B, C, D, E, F, G>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params6DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G> PartialPath<Params6<A, B, C, D, E, F>>.div(path: Path<G?>): PathDone<Params7<A, B, C, D, E, F, G?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params7DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any> PartialPath<Params7<A, B, C, D, E, F, G>>.div(
    path: Path<H>
): PartialPath<Params8<A, B, C, D, E, F, G, H>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params7DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H> PartialPath<Params7<A, B, C, D, E, F, G>>.div(
    path: Path<H?>
): PathDone<Params8<A, B, C, D, E, F, G, H?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params8DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any> PartialPath<Params8<A, B, C, D, E, F, G, H>>.div(
    path: Path<I>
): PartialPath<Params9<A, B, C, D, E, F, G, H, I>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params8DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I> PartialPath<Params8<A, B, C, D, E, F, G, H>>.div(
    path: Path<I?>
): PathDone<Params9<A, B, C, D, E, F, G, H, I?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params9DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any> PartialPath<Params9<A, B, C, D, E, F, G, H, I>>.div(
    path: Path<J>
): PartialPath<Params10<A, B, C, D, E, F, G, H, I, J>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params9DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J> PartialPath<Params9<A, B, C, D, E, F, G, H, I>>.div(
    path: Path<J?>
): PathDone<Params10<A, B, C, D, E, F, G, H, I, J?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params10DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any> PartialPath<Params10<A, B, C, D, E, F, G, H, I, J>>.div(
    path: Path<K>
): PartialPath<Params11<A, B, C, D, E, F, G, H, I, J, K>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params10DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K> PartialPath<Params10<A, B, C, D, E, F, G, H, I, J>>.div(
    path: Path<K?>
): PathDone<Params11<A, B, C, D, E, F, G, H, I, J, K?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params11DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any> PartialPath<Params11<A, B, C, D, E, F, G, H, I, J, K>>.div(
    path: Path<L>
): PartialPath<Params12<A, B, C, D, E, F, G, H, I, J, K, L>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params11DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L> PartialPath<Params11<A, B, C, D, E, F, G, H, I, J, K>>.div(
    path: Path<L?>
): PathDone<Params12<A, B, C, D, E, F, G, H, I, J, K, L?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params12DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any> PartialPath<Params12<A, B, C, D, E, F, G, H, I, J, K, L>>.div(
    path: Path<M>
): PartialPath<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params12DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M> PartialPath<Params12<A, B, C, D, E, F, G, H, I, J, K, L>>.div(
    path: Path<M?>
): PathDone<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params13DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any> PartialPath<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>>.div(
    path: Path<N>
): PartialPath<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params13DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N> PartialPath<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>>.div(
    path: Path<N?>
): PathDone<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params14DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any> PartialPath<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>>.div(
    path: Path<O>
): PartialPath<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params14DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O> PartialPath<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>>.div(
    path: Path<O?>
): PathDone<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params15DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any> PartialPath<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>>.div(
    path: Path<P>
): PartialPath<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params15DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P> PartialPath<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>>.div(
    path: Path<P?>
): PathDone<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params16DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any> PartialPath<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>>.div(
    path: Path<Q>
): PartialPath<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params16DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q> PartialPath<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>>.div(
    path: Path<Q?>
): PathDone<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params17DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any, R : Any> PartialPath<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>>.div(
    path: Path<R>
): PartialPath<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params17DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any, R> PartialPath<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>>.div(
    path: Path<R?>
): PathDone<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params18DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any, R : Any, S : Any> PartialPath<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>>.div(
    path: Path<S>
): PartialPath<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params18DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any, R : Any, S> PartialPath<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>>.div(
    path: Path<S?>
): PathDone<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params19DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any, R : Any, S : Any, T : Any> PartialPath<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>>.div(
    path: Path<T>
): PartialPath<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params19DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any, R : Any, S : Any, T> PartialPath<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>>.div(
    path: Path<T?>
): PathDone<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params20DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any, R : Any, S : Any, T : Any, U : Any> PartialPath<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>>.div(
    path: Path<U>
): PartialPath<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params20DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any, R : Any, S : Any, T : Any, U> PartialPath<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>>.div(
    path: Path<U?>
): PathDone<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params21DivPathParameter")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any, R : Any, S : Any, T : Any, U : Any, V : Any> PartialPath<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>>.div(
    path: Path<V>
): PartialPath<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>> =
    PartialPath(segments + Pair(path.name, path), arity = arity + 1)

@JvmName("params21DivPathParameterNullable")
operator fun <A : Any, B : Any, C : Any, D : Any, E : Any, F : Any, G : Any, H : Any, I : Any, J : Any, K : Any, L : Any, M : Any, N : Any, O : Any, P : Any, Q : Any, R : Any, S : Any, T : Any, U : Any, V> PartialPath<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>>.div(
    path: Path<V?>
): PathDone<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V?>> =
    PathDone(segments + Pair(path.name, path), arity = arity + 1)

// </editor-fold>