@file:Suppress("UNCHECKED_CAST")
package com.example

fun <Input, A> Route<Unit, Unit>.cookie(cookie: Parameter.Cookie<A>): Route<A, Unit> =
    addCookie(cookie) as Route<A, Unit>

@JvmName("cookieParams1")
fun <Input, A> Route<Input, Unit>.cookie(cookie: Parameter.Cookie<A>): Route<Params2<Input, A>, Unit> =
    addCookie(cookie) as Route<Params2<Input, A>, Unit>

@JvmName("cookieParams2")
fun <A, B, C> Route<Params2<A, B>, Unit>.cookie(cookie: Parameter.Cookie<C>): Route<Params3<A, B, C>, Unit> =
    addCookie(cookie) as Route<Params3<A, B, C>, Unit>

@JvmName("cookieParams3")
fun <A, B, C, D> Route<Params3<A, B, C>, Unit>.cookie(cookie: Parameter.Cookie<D>): Route<Params4<A, B, C, D>, Unit> =
    addCookie(cookie) as Route<Params4<A, B, C, D>, Unit>

@JvmName("cookieParams4")
fun <A, B, C, D, E> Route<Params4<A, B, C, D>, Unit>.cookie(cookie: Parameter.Cookie<E>): Route<Params5<A, B, C, D, E>, Unit> =
    addCookie(cookie) as Route<Params5<A, B, C, D, E>, Unit>

@JvmName("cookieParams5")
fun <A, B, C, D, E, F> Route<Params5<A, B, C, D, E>, Unit>.cookie(cookie: Parameter.Cookie<F>): Route<Params6<A, B, C, D, E, F>, Unit> =
    addCookie(cookie) as Route<Params6<A, B, C, D, E, F>, Unit>

@JvmName("cookieParams6")
fun <A, B, C, D, E, F, G> Route<Params6<A, B, C, D, E, F>, Unit>.cookie(cookie: Parameter.Cookie<G>): Route<Params7<A, B, C, D, E, F, G>, Unit> =
    addCookie(cookie) as Route<Params7<A, B, C, D, E, F, G>, Unit>

@JvmName("cookieParams7")
fun <A, B, C, D, E, F, G, H> Route<Params7<A, B, C, D, E, F, G>, Unit>.cookie(cookie: Parameter.Cookie<H>): Route<Params8<A, B, C, D, E, F, G, H>, Unit> =
    addCookie(cookie) as Route<Params8<A, B, C, D, E, F, G, H>, Unit>

@JvmName("cookieParams8")
fun <A, B, C, D, E, F, G, H, I> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.cookie(cookie: Parameter.Cookie<I>): Route<Params9<A, B, C, D, E, F, G, H, I>, Unit> =
    addCookie(cookie) as Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>

@JvmName("cookieParams9")
fun <A, B, C, D, E, F, G, H, I, J> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.cookie(cookie: Parameter.Cookie<J>): Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit> =
    addCookie(cookie) as Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>

@JvmName("cookieParams10")
fun <A, B, C, D, E, F, G, H, I, J, K> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.cookie(cookie: Parameter.Cookie<K>): Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit> =
    addCookie(cookie) as Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>

@JvmName("cookieParams11")
fun <A, B, C, D, E, F, G, H, I, J, K, L> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.cookie(cookie: Parameter.Cookie<L>): Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit> =
    addCookie(cookie) as Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>

@JvmName("cookieParams12")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.cookie(cookie: Parameter.Cookie<M>): Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit> =
    addCookie(cookie) as Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>

@JvmName("cookieParams13")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.cookie(
    cookie: Parameter.Cookie<N>
): Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit> =
    addCookie(cookie) as Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>

@JvmName("cookieParams14")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.cookie(
    cookie: Parameter.Cookie<O>
): Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit> =
    addCookie(cookie) as Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>

@JvmName("cookieParams15")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.cookie(
    cookie: Parameter.Cookie<P>
): Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit> =
    addCookie(cookie) as Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>

@JvmName("cookieParams16")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.cookie(
    cookie: Parameter.Cookie<Q>
): Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit> =
    addCookie(cookie) as Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>

@JvmName("cookieParams17")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.cookie(
    cookie: Parameter.Cookie<R>
): Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit> =
    addCookie(cookie) as Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>

@JvmName("cookieParams18")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.cookie(
    cookie: Parameter.Cookie<S>
): Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit> =
    addCookie(cookie) as Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>

@JvmName("cookieParams19")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.cookie(
    cookie: Parameter.Cookie<T>
): Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit> =
    addCookie(cookie) as Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>

@JvmName("cookieParams20")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.cookie(
    cookie: Parameter.Cookie<U>
): Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit> =
    addCookie(cookie) as Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>

@JvmName("cookieParams21")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.cookie(
    cookie: Parameter.Cookie<V>
): Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, Unit> =
    addCookie(cookie) as Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, Unit>
