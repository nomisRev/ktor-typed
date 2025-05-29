//@file:Suppress("UNCHECKED_CAST")
//
//package com.example
//
//import kotlin.collections.plus
//
//private fun <A, B> Route<*, *>.addHeader(header: Parameter.Header<*>): Route<A, B> = Route(
//    path,
//    parameters + header,
//    transform,
//    reverse
//)
//
////private fun <A, B> Route<*, *>.addHeader(headers: Parameter.Headers): Route<A, B> = Route(
////    path,
////    parameters + headers,
////    transform,
////    reverse
////)
//
//@JvmName("headerOrNullUnit")
//fun Route<Unit, Unit>.headerOrNull(name: String, defaultValue: String? = null): Route<String?, Unit> =
//    addHeader(Parameter.Header.Optional(name, defaultValue, ))
//
//@JvmName("headerOrNullInput")
//fun <Input> Route<Input, Unit>.headerOrNull(name: String): Route<Params2<Input, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams2")
//fun <A, B> Route<Params2<A, B>, Unit>.headerOrNull(name: String): Route<Params3<A, B, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams3")
//fun <A, B, C> Route<Params3<A, B, C>, Unit>.headerOrNull(name: String): Route<Params4<A, B, C, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams4")
//fun <A, B, C, D> Route<Params4<A, B, C, D>, Unit>.headerOrNull(name: String): Route<Params5<A, B, C, D, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams5")
//fun <A, B, C, D, E> Route<Params5<A, B, C, D, E>, Unit>.headerOrNull(name: String): Route<Params6<A, B, C, D, E, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams6")
//fun <A, B, C, D, E, F> Route<Params6<A, B, C, D, E, F>, Unit>.headerOrNull(name: String): Route<Params7<A, B, C, D, E, F, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams7")
//fun <A, B, C, D, E, F, G> Route<Params7<A, B, C, D, E, F, G>, Unit>.headerOrNull(name: String): Route<Params8<A, B, C, D, E, F, G, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams8")
//fun <A, B, C, D, E, F, G, H> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.headerOrNull(name: String): Route<Params9<A, B, C, D, E, F, G, H, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams9")
//fun <A, B, C, D, E, F, G, H, I> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.headerOrNull(name: String): Route<Params10<A, B, C, D, E, F, G, H, I, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams10")
//fun <A, B, C, D, E, F, G, H, I, J> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.headerOrNull(name: String): Route<Params11<A, B, C, D, E, F, G, H, I, J, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams11")
//fun <A, B, C, D, E, F, G, H, I, J, K> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.headerOrNull(name: String): Route<Params12<A, B, C, D, E, F, G, H, I, J, K, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams12")
//fun <A, B, C, D, E, F, G, H, I, J, K, L> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.headerOrNull(name: String): Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams13")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.headerOrNull(
//    name: String
//): Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams14")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.headerOrNull(
//    name: String
//): Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams15")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.headerOrNull(
//    name: String
//): Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams16")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.headerOrNull(
//    name: String
//): Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams17")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.headerOrNull(
//    name: String
//): Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams18")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.headerOrNull(
//    name: String
//): Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams19")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.headerOrNull(
//    name: String
//): Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams20")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.headerOrNull(
//    name: String
//): Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerOrNullParams21")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.headerOrNull(
//    name: String
//): Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//fun Route<Unit, Unit>.header(name: String): Route<String, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerInput")
//fun <Input> Route<Input, Unit>.header(name: String): Route<Params2<Input, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams2")
//fun <A, B> Route<Params2<A, B>, Unit>.header(name: String): Route<Params3<A, B, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams3")
//fun <A, B, C> Route<Params3<A, B, C>, Unit>.header(name: String): Route<Params4<A, B, C, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams4")
//fun <A, B, C, D> Route<Params4<A, B, C, D>, Unit>.header(name: String): Route<Params5<A, B, C, D, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams5")
//fun <A, B, C, D, E> Route<Params5<A, B, C, D, E>, Unit>.header(name: String): Route<Params6<A, B, C, D, E, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams6")
//fun <A, B, C, D, E, F> Route<Params6<A, B, C, D, E, F>, Unit>.header(name: String): Route<Params7<A, B, C, D, E, F, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams7")
//fun <A, B, C, D, E, F, G> Route<Params7<A, B, C, D, E, F, G>, Unit>.header(name: String): Route<Params8<A, B, C, D, E, F, G, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams8")
//fun <A, B, C, D, E, F, G, H> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.header(name: String): Route<Params9<A, B, C, D, E, F, G, H, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams9")
//fun <A, B, C, D, E, F, G, H, I> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.header(name: String): Route<Params10<A, B, C, D, E, F, G, H, I, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams10")
//fun <A, B, C, D, E, F, G, H, I, J> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.header(name: String): Route<Params11<A, B, C, D, E, F, G, H, I, J, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams11")
//fun <A, B, C, D, E, F, G, H, I, J, K> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.header(name: String): Route<Params12<A, B, C, D, E, F, G, H, I, J, K, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams12")
//fun <A, B, C, D, E, F, G, H, I, J, K, L> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.header(name: String): Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams13")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.header(
//    name: String
//): Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams14")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.header(
//    name: String
//): Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams15")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.header(
//    name: String
//): Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams16")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.header(
//    name: String
//): Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams17")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.header(
//    name: String
//): Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams18")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.header(
//    name: String
//): Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams19")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.header(
//    name: String
//): Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams20")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.header(
//    name: String
//): Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//@JvmName("headerParams21")
//fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.header(
//    name: String
//): Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, String>, Unit> =
//    addHeader(Parameter.Header(name, false))
//
//// TODO https://youtrack.jetbrains.com/issue/KTOR-7824/Ktor-doesnt-parse-multiple-headers
////
////@JvmName("headerUnit")
////fun Route<Unit, Unit>.headers(name: Strings): Route<List<String>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerInput")
////fun <Input> Route<Input, Unit>.headers(name: Strings): Route<Params2<Input, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams2")
////fun <A, B> Route<Params2<A, B>, Unit>.headers(name: Strings): Route<Params3<A, B, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams3")
////fun <A, B, C> Route<Params3<A, B, C>, Unit>.headers(name: Strings): Route<Params4<A, B, C, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams4")
////fun <A, B, C, D> Route<Params4<A, B, C, D>, Unit>.headers(name: Strings): Route<Params5<A, B, C, D, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams5")
////fun <A, B, C, D, E> Route<Params5<A, B, C, D, E>, Unit>.headers(name: Strings): Route<Params6<A, B, C, D, E, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams6")
////fun <A, B, C, D, E, F> Route<Params6<A, B, C, D, E, F>, Unit>.headers(name: Strings): Route<Params7<A, B, C, D, E, F, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams7")
////fun <A, B, C, D, E, F, G> Route<Params7<A, B, C, D, E, F, G>, Unit>.headers(name: Strings): Route<Params8<A, B, C, D, E, F, G, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams8")
////fun <A, B, C, D, E, F, G, H> Route<Params8<A, B, C, D, E, F, G, H>, Unit>.headers(name: Strings): Route<Params9<A, B, C, D, E, F, G, H, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams9")
////fun <A, B, C, D, E, F, G, H, I> Route<Params9<A, B, C, D, E, F, G, H, I>, Unit>.headers(name: Strings): Route<Params10<A, B, C, D, E, F, G, H, I, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams10")
////fun <A, B, C, D, E, F, G, H, I, J> Route<Params10<A, B, C, D, E, F, G, H, I, J>, Unit>.headers(name: Strings): Route<Params11<A, B, C, D, E, F, G, H, I, J, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams11")
////fun <A, B, C, D, E, F, G, H, I, J, K> Route<Params11<A, B, C, D, E, F, G, H, I, J, K>, Unit>.headers(name: Strings): Route<Params12<A, B, C, D, E, F, G, H, I, J, K, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams12")
////fun <A, B, C, D, E, F, G, H, I, J, K, L> Route<Params12<A, B, C, D, E, F, G, H, I, J, K, L>, Unit>.headers(name: Strings): Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams13")
////fun <A, B, C, D, E, F, G, H, I, J, K, L, M> Route<Params13<A, B, C, D, E, F, G, H, I, J, K, L, M>, Unit>.headers(
////    name: Strings
////): Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams14")
////fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Route<Params14<A, B, C, D, E, F, G, H, I, J, K, L, M, N>, Unit>.headers(
////    name: Strings
////): Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams15")
////fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Route<Params15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O>, Unit>.headers(
////    name: Strings
////): Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams16")
////fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Route<Params16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P>, Unit>.headers(
////    name: Strings
////): Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams17")
////fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Route<Params17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q>, Unit>.headers(
////    name: Strings
////): Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams18")
////fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Route<Params18<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R>, Unit>.headers(
////    name: Strings
////): Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams19")
////fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Route<Params19<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S>, Unit>.headers(
////    name: Strings
////): Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams20")
////fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Route<Params20<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T>, Unit>.headers(
////    name: Strings
////): Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
////
////@JvmName("headerParams21")
////fun <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Route<Params21<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U>, Unit>.headers(
////    name: Strings
////): Route<Params22<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, List<String>>, Unit> =
////    addHeader(Parameter.Header(name, false))
