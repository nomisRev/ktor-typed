@file:Suppress("UNCHECKED_CAST")

package com.example


@JvmName("headerUnit")
fun <Input, A> Route<Unit, Unit>.header(header: Parameter.Header): Route<List<String>, Unit> =
    addHeader(header) as Route<List<String>, Unit>

@JvmName("headerInput")
fun <Input> Route<Input, Unit>.header(header: Parameter.Header): Route<Params2<Input, List<String>>, Unit> =
    addHeader(header) as Route<Params2<Input, List<String>>, Unit>

@JvmName("headerParams2")
fun <A, B> Route<Params2<A, B>, Unit>.header(header: Parameter.Header): Route<Params3<A, B, List<String>>, Unit> =
    addHeader(header) as Route<Params3<A, B, List<String>>, Unit>

@JvmName("headerParams3")
fun <A, B, C> Route<Params3<A, B, C>, Unit>.header(header: Parameter.Header): Route<Params4<A, B, C, List<String>>, Unit> =
    addHeader(header) as Route<Params4<A, B, C, List<String>>, Unit>

@JvmName("headerParams4")
fun <A, B, C, D> Route<Params4<A, B, C, D>, Unit>.header(header: Parameter.Header): Route<Params5<A, B, C, D, List<String>>, Unit> =
    addHeader(header) as Route<Params5<A, B, C, D, List<String>>, Unit>

@JvmName("headerParams5")
fun <A, B, C, D, E> Route<Params5<A, B, C, D, E>, Unit>.header(header: Parameter.Header): Route<Params6<A, B, C, D, E, List<String>>, Unit> =
    addHeader(header) as Route<Params6<A, B, C, D, E, List<String>>, Unit>

@JvmName("headerParams6")
fun <A, B, C, D, E, F> Route<Params6<A, B, C, D, E, F>, Unit>.header(header: Parameter.Header): Route<Params7<A, B, C, D, E, F, List<String>>, Unit> =
    addHeader(header) as Route<Params7<A, B, C, D, E, F, List<String>>, Unit>

@JvmName("headerParams7")
fun <A, B, C, D, E, F, G> Route<Params7<A, B, C, D, E, F, G>, Unit>.header(header: Parameter.Header): Route<Params8<A, B, C, D, E, F, G, List<String>>, Unit> =
    addHeader(header) as Route<Params8<A, B, C, D, E, F, G, List<String>>, Unit>

@JvmName("headerParams8")
fun <A, B, C, D, E, F, G, H> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.header(header: Parameter.Header): Route<Params9<A, B, C, D, E, F, G, H, List<String>>, Unit> =
    addHeader(header) as Route<Params9<A, B, C, D, E, F, G, H, List<String>>, Unit>

@JvmName("headerParams9")
fun <A, B, C, D, E, F, G, H, I> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.header(header: Parameter.Header): Route<Params10<A, B, C, D, E, F, G, H, I, List<String>>, Unit> =
    addHeader(header) as Route<Params10<A, B, C, D, E, F, G, H, I, List<String>>, Unit>

@JvmName("headerParams10")
fun <A, B, C, D, E, F, G, H, I, J> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.header(header: Parameter.Header): Route<Params11<A, B, C, D, E, F, G, H, I, J, List<String>>, Unit> =
    addHeader(header) as Route<Params11<A, B, C, D, E, F, G, H, I, J, List<String>>, Unit>

@JvmName("headerParams11")
fun <A, B, C, D, E, F, G, H, I, J, K> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.header(header: Parameter.Header): Route<Params12<A, B, C, D, E, F, G, H, I, J, K, List<String>>, Unit> =
    addHeader(header) as Route<Params12<A, B, C, D, E, F, G, H, I, J, K, List<String>>, Unit>

@JvmName("headerParams12")
fun <A, B, C, D, E, F, G, H, I, J, K, L> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.header(header: Parameter.Header): Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, List<String>>, Unit> =
    addHeader(header) as Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, List<String>>, Unit>

@JvmName("headerParams13")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.header(
    header: Parameter.Header
): Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, List<String>>, Unit> =
    addHeader(header) as Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, List<String>>, Unit>

@JvmName("headerParams14")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.header(
    header: Parameter.Header
): Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, List<String>>, Unit> =
    addHeader(header) as Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, List<String>>, Unit>

@JvmName("headerParams15")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.header(
    header: Parameter.Header
): Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, List<String>>, Unit> =
    addHeader(header) as Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, List<String>>, Unit>

@JvmName("headerParams16")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.header(
    header: Parameter.Header
): Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, List<String>>, Unit> =
    addHeader(header) as Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, List<String>>, Unit>

@JvmName("headerParams17")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.header(
    header: Parameter.Header
): Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, List<String>>, Unit> =
    addHeader(header) as Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, List<String>>, Unit>

@JvmName("headerParams18")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.header(
    header: Parameter.Header
): Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, List<String>>, Unit> =
    addHeader(header) as Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, List<String>>, Unit>

@JvmName("headerParams19")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.header(
    header: Parameter.Header
): Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, List<String>>, Unit> =
    addHeader(header) as Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, List<String>>, Unit>

@JvmName("headerParams20")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.header(
    header: Parameter.Header
): Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, List<String>>, Unit> =
    addHeader(header) as Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, List<String>>, Unit>

@JvmName("headerParams21")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.header(
    header: Parameter.Header
): Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, List<String>>, Unit> =
    addHeader(header) as Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, List<String>>, Unit>
