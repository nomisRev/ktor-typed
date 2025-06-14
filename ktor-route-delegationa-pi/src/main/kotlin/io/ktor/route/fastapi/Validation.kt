package io.ktor.route.fastapi

typealias ValidationBuilder = MutableList<String>

fun interface Validation<V> {
    fun ValidationBuilder.validate(value: V): V

    companion object {
        fun int(): Validation<Int> = Validation { it }
    }
}

fun Validation<Int>.min(min: Int) = Validation<Int> {
    val it = validate(it)
    if (it < min) add("Must be greater than or equal to $min")
    it
}

fun Validation<Int>.max(max: Int, message: String = "Must be greater than or equal to") = Validation<Int> {
    val it = validate(it)
    if (it > max) add("$message: $max")
    it
}