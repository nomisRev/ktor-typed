package io.github.nomisrev.typedapi

/**
 * Interface that allows a value to be converted into an `HttpRequest`,
 * this behavior is provided in a generic and performant manner.
 */
interface HttpRequestValue {
    fun path(): String
    fun query(block: (value: Any?, input: Input.Query<Any?>) -> Unit)
    fun path(block: (value: Any?, input: Input.Path<Any?>) -> Unit)
    fun header(block: (value: Any?, input: Input.Header<Any?>) -> Unit)
    fun body(block: (value: Any?, input: Input.Body<Any?>) -> Unit)
}
