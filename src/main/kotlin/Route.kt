package com.example

class Route<Input, Output>(
    val path: PathResult<*>,
    val parameters: List<Parameter<*>>,
    val transform: suspend (Any?) -> Any?,
    val reverse: suspend (Any?) -> Any?
) {
    val arity get() = path.arity + parameters.size

    internal fun addHeader(header: Parameter.Header): Route<Input, Output> = Route(
        path,
        parameters + header,
        transform,
        reverse
    )

    internal fun addCookie(cookie: Parameter.Cookie): Route<Input, Output> = Route(
        path,
        parameters + cookie,
        transform,
        reverse
    )
}
