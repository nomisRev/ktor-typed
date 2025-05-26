@file:Suppress("UNCHECKED_CAST")
package com.example


fun <A> Route<Unit, Unit>.cookie(cookie: Parameter.Cookie): Route<String, Unit> =
    addCookie(cookie) as Route<String, Unit>

@JvmName("cookieParams1")
fun <Input> Route<Input, Unit>.cookie(cookie: Parameter.Cookie): Route<Params2<Input, String>, Unit> =
    addCookie(cookie) as Route<Params2<Input, String>, Unit>


@JvmName("cookieParams2")
fun <A, B> Route<Params2<A, B>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params3<A, B, String>, Unit> =
    addCookie(cookie) as Route<Params3<A, B, String>, Unit>

@JvmName("cookieParams3")
fun <A, B, C> Route<Params3<A, B, C>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params4<A, B, C, String>, Unit> =
    addCookie(cookie) as Route<Params4<A, B, C, String>, Unit>

@JvmName("cookieParams4")
fun <A, B, C, D> Route<Params4<A, B, C, D>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params5<A, B, C, D, String>, Unit> =
    addCookie(cookie) as Route<Params5<A, B, C, D, String>, Unit>

@JvmName("cookieParams5")
fun <A, B, C, D, E> Route<Params5<A, B, C, D, E>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params6<A, B, C, D, E, String>, Unit> =
    addCookie(cookie) as Route<Params6<A, B, C, D, E, String>, Unit>


@JvmName("cookieParams6")
fun <A, B, C, D, E, F> Route<Params6<A, B, C, D, E, F>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params7<A, B, C, D, E, F, String>, Unit> =
    addCookie(cookie) as Route<Params7<A, B, C, D, E, F, String>, Unit>

@JvmName("cookieParams7")
fun <A, B, C, D, E, F, G> Route<Params7<A, B, C, D, E, F, G>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params8<A, B, C, D, E, F, G, String>, Unit> =
    addCookie(cookie) as Route<Params8<A, B, C, D, E, F, G, String>, Unit>

@JvmName("cookieParams8")
fun <A, B, C, D, E, F, G, H> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params9<A, B, C, D, E, F, G, H, String>, Unit> =
    addCookie(cookie) as Route<Params9<A, B, C, D, E, F, G, H, String>, Unit>

@JvmName("cookieParams9")
fun <A, B, C, D, E, F, G, H, I> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params10<A, B, C, D, E, F, G, H, I, String>, Unit> =
    addCookie(cookie) as Route<Params10<A, B, C, D, E, F, G, H, I, String>, Unit>

@JvmName("cookieParams10")
fun <A, B, C, D, E, F, G, H, I, J> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params11<A, B, C, D, E, F, G, H, I, J, String>, Unit> =
    addCookie(cookie) as Route<Params11<A, B, C, D, E, F, G, H, I, J, String>, Unit>

@JvmName("cookieParams11")
fun <A, B, C, D, E, F, G, H, I, J, K> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params12<A, B, C, D, E, F, G, H, I, J, K, String>, Unit> =
    addCookie(cookie) as Route<Params12<A, B, C, D, E, F, G, H, I, J, K, String>, Unit>

@JvmName("cookieParams12")
fun <A, B, C, D, E, F, G, H, I, J, K, L> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.cookie(cookie: Parameter.Cookie): Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, String>, Unit> =
    addCookie(cookie) as Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, String>, Unit>

@JvmName("cookieParams13")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.cookie(
    cookie: Parameter.Cookie
): Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, String>, Unit> =
    addCookie(cookie) as Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, String>, Unit>

@JvmName("cookieParams14")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.cookie(
    cookie: Parameter.Cookie
): Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, String>, Unit> =
    addCookie(cookie) as Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, String>, Unit>

@JvmName("cookieParams15")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.cookie(
    cookie: Parameter.Cookie
): Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String>, Unit> =
    addCookie(cookie) as Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String>, Unit>

@JvmName("cookieParams16")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.cookie(
    cookie: Parameter.Cookie
): Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String>, Unit> =
    addCookie(cookie) as Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String>, Unit>

@JvmName("cookieParams17")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.cookie(
    cookie: Parameter.Cookie
): Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String>, Unit> =
    addCookie(cookie) as Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String>, Unit>

@JvmName("cookieParams18")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.cookie(
    cookie: Parameter.Cookie
): Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String>, Unit> =
    addCookie(cookie) as Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String>, Unit>

@JvmName("cookieParams19")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.cookie(
    cookie: Parameter.Cookie
): Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String>, Unit> =
    addCookie(cookie) as Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String>, Unit>

@JvmName("cookieParams20")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.cookie(
    cookie: Parameter.Cookie
): Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String>, Unit> =
    addCookie(cookie) as Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String>, Unit>

@JvmName("cookieParams21")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.cookie(
    cookie: Parameter.Cookie
): Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String>, Unit> =
    addCookie(cookie) as Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String>, Unit>
