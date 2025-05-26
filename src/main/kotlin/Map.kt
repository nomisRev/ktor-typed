@file:Suppress("UNCHECKED_CAST")

package com.example

fun <A, B> Route<A, Unit>.map(map: suspend (A) -> B, imap: suspend (B) -> A): Route<B, Unit> =
    Route(path, parameters, { map(this.transform(it) as A) }) {
        imap(this.reverse(it) as B)
    }

fun <A, B, C> Route<Params2<A, B>, Unit>.map(transform: suspend (A, B) -> C, imap: suspend (C) -> Params2<A, B>): Route<C, Unit> =
    Route(path, parameters, {
        val (a, b) = this.transform(it) as Params2<A, B>
        transform(a, b)
    }) { imap(this.reverse(it) as C) }

fun <A, B, C, D> Route<Params3<A, B, C>, Unit>.map(transform: suspend (A, B, C) -> D, imap: suspend (D) -> Params3<A, B, C>): Route<D, Unit> =
    Route(path, parameters, {
        val (a, b, c) = this.transform(it) as Params3<A, B, C>
        transform(a, b, c)
    }) { imap(this.reverse(it) as D) }

fun <A, B, C, D, E> Route<Params4<A, B, C, D>, Unit>.map(transform: suspend (A, B, C, D) -> E, imap: suspend (E) -> Params4<A, B, C, D>): Route<E, Unit> =
    Route(path, parameters, {
        val (a, b, c, d) = this.transform(it) as Params4<A, B, C, D>
        transform(a, b, c, d)
    }) { imap(this.reverse(it) as E) }

fun <A, B, C, D, E, F> Route<Params5<A, B, C, D, E>, Unit>.map(
    transform: suspend (A, B, C, D, E) -> F,
    imap: suspend (F) -> Params5<A, B, C, D, E>
): Route<F, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e) = this.transform(it) as Params5<A, B, C, D, E>
        transform(a, b, c, d, e)
    }) { imap(this.reverse(it) as F) }

fun <A, B, C, D, E, F, G> Route<Params6<A, B, C, D, E, F>, Unit>.map(transform: suspend (A, B, C, D, E, F) -> G, imap: suspend (G) -> Params6<A, B, C, D, E, F>): Route<G, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f) = this.transform(it) as Params6<A, B, C, D, E, F>
        transform(a, b, c, d, e, f)
    }) { imap(this.reverse(it) as G) }

