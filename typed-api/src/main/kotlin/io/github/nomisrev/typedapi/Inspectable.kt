package io.github.nomisrev.typedapi

interface Inspectable {
    fun query(block: (value: Any?, input: Input.Query<Any?>) -> Unit)
    fun path(block: (value: Any?, input: Input.Path<Any?>) -> Unit)
    fun header(block: (value: Any?, input: Input.Header<Any?>) -> Unit)
    fun body(block: (value: Any?, input: Input.Body<Any?>) -> Unit)
}
