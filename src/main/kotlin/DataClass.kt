package com.example

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
fun <A, F : Any> Route<A, Unit>.asDataClass(
    kClass: KClass<F>,
    block: (A) -> F
): Route<F, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params1<A>
        block.invoke(
            params.value
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, reified F : Any> Route<Params2<A, B>, Unit>.asDataClass(
    noinline block: (A, B) -> F
): Route<F, Unit> =
    asDataClass(F::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, F : Any> Route<Params2<A, B>, Unit>.asDataClass(
    kClass: KClass<F>,
    block: (A, B) -> F
): Route<F, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params2<A, B>
        block.invoke(
            params.first,
            params.second
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, reified F : Any> Route<Params3<A, B, C>, Unit>.asDataClass(
    noinline block: (A, B, C) -> F
): Route<F, Unit> =
    asDataClass(F::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, F : Any> Route<Params3<A, B, C>, Unit>.asDataClass(
    kClass: KClass<F>,
    block: (A, B, C) -> F
): Route<F, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params3<A, B, C>
        block.invoke(
            params.first,
            params.second,
            params.third
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, reified F : Any> Route<Params4<A, B, C, D>, Unit>.asDataClass(
    noinline block: (A, B, C, D) -> F
): Route<F, Unit> =
    asDataClass(F::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, F : Any> Route<Params4<A, B, C, D>, Unit>.asDataClass(
    kClass: KClass<F>,
    block: (A, B, C, D) -> F
): Route<F, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params4<A, B, C, D>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, reified F : Any> Route<Params5<A, B, C, D, E>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E) -> F
): Route<F, Unit> =
    asDataClass(F::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F : Any> Route<Params5<A, B, C, D, E>, Unit>.asDataClass(
    kClass: KClass<F>,
    block: (A, B, C, D, E) -> F
): Route<F, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params5<A, B, C, D, E>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, reified G : Any> Route<Params6<A, B, C, D, E, F>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F) -> G
): Route<G, Unit> =
    asDataClass(G::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G : Any> Route<Params6<A, B, C, D, E, F>, Unit>.asDataClass(
    kClass: KClass<G>,
    block: (A, B, C, D, E, F) -> G
): Route<G, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params6<A, B, C, D, E, F>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, reified H : Any> Route<Params7<A, B, C, D, E, F, G>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G) -> H
): Route<H, Unit> =
    asDataClass(H::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H : Any> Route<Params7<A, B, C, D, E, F, G>, Unit>.asDataClass(
    kClass: KClass<H>,
    block: (A, B, C, D, E, F, G) -> H
): Route<H, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params7<A, B, C, D, E, F, G>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, reified I : Any> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H) -> I
): Route<I, Unit> =
    asDataClass(I::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I : Any> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.asDataClass(
    kClass: KClass<I>,
    block: (A, B, C, D, E, F, G, H) -> I
): Route<I, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params8<A, B, C, D, E, F, G, H>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, reified J : Any> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I) -> J
): Route<J, Unit> =
    asDataClass(J::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J : Any> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.asDataClass(
    kClass: KClass<J>,
    block: (A, B, C, D, E, F, G, H, I) -> J
): Route<J, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params9<A, B, C, D, E, F, G, H, I>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, reified K : Any> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J) -> K
): Route<K, Unit> =
    asDataClass(K::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K : Any> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.asDataClass(
    kClass: KClass<K>,
    block: (A, B, C, D, E, F, G, H, I, J) -> K
): Route<K, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params10<A, B, C, D, E, F, G, H, I, J>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, reified L : Any> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K) -> L
): Route<L, Unit> =
    asDataClass(L::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L : Any> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.asDataClass(
    kClass: KClass<L>,
    block: (A, B, C, D, E, F, G, H, I, J, K) -> L
): Route<L, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params11<A, B, C, D, E, F, G, H, I, J, K>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, reified M : Any> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L) -> M
): Route<M, Unit> =
    asDataClass(M::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M : Any> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.asDataClass(
    kClass: KClass<M>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L) -> M
): Route<M, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params12<A, B, C, D, E, F, G, H, I, J, K, L>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, reified N : Any> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> N
): Route<N, Unit> =
    asDataClass(N::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N : Any> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.asDataClass(
    kClass: KClass<N>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> N
): Route<N, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth,
            params.thirteenth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, reified O : Any> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> O
): Route<O, Unit> =
    asDataClass(O::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O : Any> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.asDataClass(
    kClass: KClass<O>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> O
): Route<O, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth,
            params.thirteenth,
            params.fourteenth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, reified P : Any> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> P
): Route<P, Unit> =
    asDataClass(P::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P : Any> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.asDataClass(
    kClass: KClass<P>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> P
): Route<P, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth,
            params.thirteenth,
            params.fourteenth,
            params.fifteenth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, reified Q : Any> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Q
): Route<Q, Unit> =
    asDataClass(Q::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q : Any> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.asDataClass(
    kClass: KClass<Q>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Q
): Route<Q, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth,
            params.thirteenth,
            params.fourteenth,
            params.fifteenth,
            params.sixteenth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, reified R : Any> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> R
): Route<R, Unit> =
    asDataClass(R::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R : Any> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.asDataClass(
    kClass: KClass<R>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> R
): Route<R, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth,
            params.thirteenth,
            params.fourteenth,
            params.fifteenth,
            params.sixteenth,
            params.seventeenth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, reified S : Any> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> S
): Route<S, Unit> =
    asDataClass(S::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S : Any> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.asDataClass(
    kClass: KClass<S>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> S
): Route<S, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth,
            params.thirteenth,
            params.fourteenth,
            params.fifteenth,
            params.sixteenth,
            params.seventeenth,
            params.eighteenth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, reified T : Any> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> T
): Route<T, Unit> =
    asDataClass(T::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T : Any> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.asDataClass(
    kClass: KClass<T>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> T
): Route<T, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth,
            params.thirteenth,
            params.fourteenth,
            params.fifteenth,
            params.sixteenth,
            params.seventeenth,
            params.eighteenth,
            params.nineteenth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, reified U : Any> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> U
): Route<U, Unit> =
    asDataClass(U::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U : Any> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.asDataClass(
    kClass: KClass<U>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> U
): Route<U, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth,
            params.thirteenth,
            params.fourteenth,
            params.fifteenth,
            params.sixteenth,
            params.seventeenth,
            params.eighteenth,
            params.nineteenth,
            params.twentieth
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, reified V : Any> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) -> V
): Route<V, Unit> =
    asDataClass(V::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V : Any> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.asDataClass(
    kClass: KClass<V>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U) -> V
): Route<V, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth,
            params.thirteenth,
            params.fourteenth,
            params.fifteenth,
            params.sixteenth,
            params.seventeenth,
            params.eighteenth,
            params.nineteenth,
            params.twentieth,
            params.twentyFirst
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}

inline fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, reified W : Any> Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, Unit>.asDataClass(
    noinline block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) -> W
): Route<W, Unit> =
    asDataClass(W::class, block)

@Suppress("UNCHECKED_CAST")
fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W : Any> Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>, Unit>.asDataClass(
    kClass: KClass<W>,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V) -> W
): Route<W, Unit> {
    require(kClass.isData) { "Only data classes are supported." }
    val components = kClass.members.filter { it.name.startsWith("component") }
    return Route(path, parameters, { params ->
        val params = params as Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V>
        block.invoke(
            params.first,
            params.second,
            params.third,
            params.fourth,
            params.fifth,
            params.sixth,
            params.seventh,
            params.eighth,
            params.ninth,
            params.tenth,
            params.eleventh,
            params.twelfth,
            params.thirteenth,
            params.fourteenth,
            params.fifteenth,
            params.sixteenth,
            params.seventeenth,
            params.eighteenth,
            params.nineteenth,
            params.twentieth,
            params.twentyFirst,
            params.twentySecond
        )
    }) { value ->
        components.map { it.call(value) }.toParams()
    }
}
