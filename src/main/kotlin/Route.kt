package com.example

class Route<Input, Output>(
    val path: PathResult<*>,
    val parameters: List<Parameter<*>>,
    val transform: suspend (Any?) -> Any?,
    val reverse: suspend (Any?) -> Any?
) {
    val arity get() = path.arity + parameters.size
}
