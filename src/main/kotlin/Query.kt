@file:Suppress("UNCHECKED_CAST")

package com.example

import kotlin.collections.plus

private fun <A, B> Route<*, *>.addQuery(query: Parameter.Query<*>): Route<A, B> = Route(
    path,
    parameters + query,
    transform,
    reverse
)

fun <A> Route<Unit, Unit>.query(param: Parameter.Query<A>): Route<A, Unit> =
    addQuery(param)

@JvmName("queryParams1")
fun <A, B> Route<A, Unit>.query(param: Parameter.Query<B>): Route<Params2<A, B>, Unit> =
    addQuery(param)

@JvmName("queryParams2")
fun <A, B, C> Route<Params2<A, B>, Unit>.query(param: Parameter.Query<C>): Route<Params3<A, B, C>, Unit> =
    addQuery(param)

@JvmName("queryParams3")
fun <A, B, C, D> Route<Params3<A, B, C>, Unit>.query(param: Parameter.Query<D>): Route<Params4<A, B, C, D>, Unit> =
    addQuery(param)

@JvmName("queryParams4")
fun <A, B, C, D, E> Route<Params4<A, B, C, D>, Unit>.query(param: Parameter.Query<E>): Route<Params5<A, B, C, D, E>, Unit> =
    addQuery(param)

@JvmName("queryParams5")
fun <A, B, C, D, E, F> Route<Params5<A, B, C, D, E>, Unit>.query(param: Parameter.Query<F>): Route<Params6<A, B, C, D, E, F>, Unit> =
    addQuery(param)

@JvmName("queryParams6")
fun <A, B, C, D, E, F, G> Route<Params6<A, B, C, D, E, F>, Unit>.query(param: Parameter.Query<G>): Route<Params7<A, B, C, D, E, F, G>, Unit> =
    addQuery(param)

@JvmName("queryParams7")
fun <A, B, C, D, E, F, G, H> Route<Params7<A, B, C, D, E, F, G>, Unit>.query(param: Parameter.Query<H>): Route<Params8<A, B, C, D, E, F, G, H>, Unit> =
    addQuery(param)

@JvmName("queryParams8")
fun <A, B, C, D, E, F, G, H, I> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.query(param: Parameter.Query<I>): Route<Params9<A, B, C, D, E, F, G, H, I>, Unit> =
    addQuery(param)

@JvmName("queryParams9")
fun <A, B, C, D, E, F, G, H, I, J> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.query(param: Parameter.Query<J>): Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit> =
    addQuery(param)

@JvmName("queryParams10")
fun <A, B, C, D, E, F, G, H, I, J, K> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.query(param: Parameter.Query<K>): Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit> =
    addQuery(param)

@JvmName("queryParams11")
fun <A, B, C, D, E, F, G, H, I, J, K, L> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.query(param: Parameter.Query<L>): Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit> =
    addQuery(param)

@JvmName("queryParams12")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.query(param: Parameter.Query<M>): Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit> =
    addQuery(param)

@JvmName("queryParams13")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.query(param: Parameter.Query<N>): Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit> =
    addQuery(param)

@JvmName("queryParams14")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.query(
    param: Parameter.Query<O>
): Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit> =
    addQuery(param)

@JvmName("queryParams15")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.query(
    param: Parameter.Query<P>
): Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit> =
    addQuery(param)

@JvmName("queryParams16")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.query(
    param: Parameter.Query<Q>
): Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit> =
    addQuery(param)

@JvmName("queryParams17")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.query(
    param: Parameter.Query<R>
): Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit> =
    addQuery(param)

@JvmName("queryParams18")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.query(
    param: Parameter.Query<S>
): Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit> =
    addQuery(param)

@JvmName("queryParams19")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.query(
    param: Parameter.Query<T>
): Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit> =
    addQuery(param)

@JvmName("queryParams20")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.query(
    param: Parameter.Query<U>
): Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit> =
    addQuery(param)

@JvmName("queryParams21")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.query(
    param: Parameter.Query<V>
): Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, Unit> =
    addQuery(param)
