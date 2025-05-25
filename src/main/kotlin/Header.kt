@file:Suppress("UNCHECKED_CAST")
package com.example

@JvmName("headerUnit")
fun <Input, A> Route<Unit, Unit>.header(header: Parameter.Header<A>): Route<A, Unit> =
    addHeader(header) as Route<A, Unit>

@JvmName("headerInput")
fun <Input, A> Route<Input, Unit>.header(header: Parameter.Header<A>): Route<Params2<Input, A>, Unit> =
    addHeader(header) as Route<Params2<Input, A>, Unit>

@JvmName("headerParams2")
fun <A, B, C> Route<Params2<A, B>, Unit>.header(header: Parameter.Header<C>): Route<Params3<A, B, C>, Unit> =
    addHeader(header) as Route<Params3<A, B, C>, Unit>

@JvmName("headerParams3")
fun <A, B, C, D> Route<Params3<A, B, C>, Unit>.header(header: Parameter.Header<D>): Route<Params4<A, B, C, D>, Unit> =
    addHeader(header) as Route<Params4<A, B, C, D>, Unit>

@JvmName("headerParams4")
fun <A, B, C, D, E> Route<Params4<A, B, C, D>, Unit>.header(header: Parameter.Header<E>): Route<Params5<A, B, C, D, E>, Unit> =
    addHeader(header) as Route<Params5<A, B, C, D, E>, Unit>

@JvmName("headerParams5")
fun <A, B, C, D, E, F> Route<Params5<A, B, C, D, E>, Unit>.header(header: Parameter.Header<F>): Route<Params6<A, B, C, D, E, F>, Unit> =
    addHeader(header) as Route<Params6<A, B, C, D, E, F>, Unit>

@JvmName("headerParams6")
fun <A, B, C, D, E, F, G> Route<Params6<A, B, C, D, E, F>, Unit>.header(header: Parameter.Header<G>): Route<Params7<A, B, C, D, E, F, G>, Unit> =
    addHeader(header) as Route<Params7<A, B, C, D, E, F, G>, Unit>

@JvmName("headerParams7")
fun <A, B, C, D, E, F, G, H> Route<Params7<A, B, C, D, E, F, G>, Unit>.header(header: Parameter.Header<H>): Route<Params8<A, B, C, D, E, F, G, H>, Unit> =
    addHeader(header) as Route<Params8<A, B, C, D, E, F, G, H>, Unit>

@JvmName("headerParams8")
fun <A, B, C, D, E, F, G, H, I> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.header(header: Parameter.Header<I>): Route<Params9<A, B, C, D, E, F, G, H, I>, Unit> =
    addHeader(header) as Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>

@JvmName("headerParams9")
fun <A, B, C, D, E, F, G, H, I, J> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.header(header: Parameter.Header<J>): Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit> =
    addHeader(header) as Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>

@JvmName("headerParams10")
fun <A, B, C, D, E, F, G, H, I, J, K> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.header(header: Parameter.Header<K>): Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit> =
    addHeader(header) as Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>

@JvmName("headerParams11")
fun <A, B, C, D, E, F, G, H, I, J, K, L> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.header(header: Parameter.Header<L>): Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit> =
    addHeader(header) as Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>

@JvmName("headerParams12")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.header(header: Parameter.Header<M>): Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit> =
    addHeader(header) as Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>

@JvmName("headerParams13")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.header(
    header: Parameter.Header<N>
): Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit> =
    addHeader(header) as Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>

@JvmName("headerParams14")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.header(
    header: Parameter.Header<O>
): Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit> =
    addHeader(header) as Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>

@JvmName("headerParams15")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.header(
    header: Parameter.Header<P>
): Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit> =
    addHeader(header) as Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>

@JvmName("headerParams16")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.header(
    header: Parameter.Header<Q>
): Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit> =
    addHeader(header) as Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>

@JvmName("headerParams17")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.header(
    header: Parameter.Header<R>
): Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit> =
    addHeader(header) as Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>

@JvmName("headerParams18")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.header(
    header: Parameter.Header<S>
): Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit> =
    addHeader(header) as Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>

@JvmName("headerParams19")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.header(
    header: Parameter.Header<T>
): Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit> =
    addHeader(header) as Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>

@JvmName("headerParams20")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.header(
    header: Parameter.Header<U>
): Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit> =
    addHeader(header) as Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>

@JvmName("headerParams21")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.header(
    header: Parameter.Header<V>
): Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, Unit> =
    addHeader(header) as Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, Unit>
