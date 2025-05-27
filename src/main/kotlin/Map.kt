@file:Suppress("UNCHECKED_CAST")

package com.example

import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.functions.FunctionN

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

fun <A, B, C, D, E, F, G, H> Route<Params7<A, B, C, D, E, F, G>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G) -> H,
    imap: suspend (H) -> Params7<A, B, C, D, E, F, G>
): Route<H, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g) = this.transform(it) as Params7<A, B, C, D, E, F, G>
        transform(a, b, c, d, e, f, g)
    }) { imap(this.reverse(it) as H) }

fun <A, B, C, D, E, F, G, H, I> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H) -> I,
    imap: suspend (I) -> Params8<A, B, C, D, E, F, G, H>
): Route<I, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h) = this.transform(it) as Params8<A, B, C, D, E, F, G, H>
        transform(a, b, c, d, e, f, g, h)
    }) { imap(this.reverse(it) as I) }

fun <A, B, C, D, E, F, G, H, I, J> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I) -> J,
    imap: suspend (J) -> Params9<A, B, C, D, E, F, G, H, I>
): Route<J, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i) = this.transform(it) as Params9<A, B, C, D, E, F, G, H, I>
        transform(a, b, c, d, e, f, g, h, i)
    }) { imap(this.reverse(it) as J) }

fun <A, B, C, D, E, F, G, H, I, J, K> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J) -> K,
    imap: suspend (K) -> Params10<A, B, C, D, E, F, G, H, I, J>
): Route<K, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j) = this.transform(it) as Params10<A, B, C, D, E, F, G, H, I, J>
        transform(a, b, c, d, e, f, g, h, i, j)
    }) { imap(this.reverse(it) as K) }

fun <A, B, C, D, E, F, G, H, I, J, K, L> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K) -> L,
    imap: suspend (L) -> Params11<A, B, C, D, E, F, G, H, I, J, K>
): Route<L, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k) = this.transform(it) as Params11<A, B, C, D, E, F, G, H, I, J, K>
        transform(a, b, c, d, e, f, g, h, i, j, k)
    }) { imap(this.reverse(it) as L) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L) -> M,
    imap: suspend (M) -> Params12<A, B, C, D, E, F, G, H, I, J, K, L>
): Route<M, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l) = this.transform(it) as Params12<A, B, C, D, E, F, G, H, I, J, K, L>
        transform(a, b, c, d, e, f, g, h, i, j, k, l)
    }) { imap(this.reverse(it) as M) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M) -> N,
    imap: suspend (N) -> Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>
): Route<N, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l, m) = this.transform(it) as Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>
        transform(a, b, c, d, e, f, g, h, i, j, k, l, m)
    }) { imap(this.reverse(it) as N) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> O,
    imap: suspend (O) -> Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>
): Route<O, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l, m, n) = this.transform(it) as Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>
        transform(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
    }) { imap(this.reverse(it) as O) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> P,
    imap: suspend (P) -> Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>
): Route<P, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o) = this.transform(it) as Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>
        transform(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o)
    }) { imap(this.reverse(it) as P) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Q,
    imap: suspend (Q) -> Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>
): Route<Q, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p) = this.transform(it) as Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>
        transform(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
    }) { imap(this.reverse(it) as Q) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> R,
    imap: suspend (R) -> Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>
): Route<R, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q) = this.transform(it) as Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>
        transform(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q)
    }) { imap(this.reverse(it) as R) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> S,
    imap: suspend (S) -> Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>
): Route<S, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r) = this.transform(it) as Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>
        transform(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r)
    }) { imap(this.reverse(it) as S) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> T,
    imap: suspend (T) -> Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>
): Route<T, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s) = this.transform(it) as Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>
        transform(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s)
    }) { imap(this.reverse(it) as T) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> U,
    imap: suspend (U) -> Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>
): Route<U, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t) = this.transform(it) as Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>
        transform(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t)
    }) { imap(this.reverse(it) as U) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) -> V,
    imap: suspend (V) -> Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>
): Route<V, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u) = this.transform(it) as Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>
        transform(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u)
    }) { imap(this.reverse(it) as V) }

fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W> Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, Unit>.map(
    transform: suspend (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) -> W,
    imap: suspend (W) -> Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>
): Route<W, Unit> =
    Route(path, parameters, {
        val (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v) = this.transform(it) as Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>
        transform(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v)
    }) { imap(this.reverse(it) as W) }
