package io.github.nomisrev.typedapi

typealias ValidationBuilder = MutableList<String>

fun interface Validation<V> {
    fun ValidationBuilder.validate(value: V): V

    companion object {
        // Primitive type validators
        fun int(): Validation<Int> = Validation { it }
        fun long(): Validation<Long> = Validation { it }
        fun double(): Validation<Double> = Validation { it }
        fun float(): Validation<Float> = Validation { it }
        fun short(): Validation<Short> = Validation { it }
        fun byte(): Validation<Byte> = Validation { it }
        fun boolean(): Validation<Boolean> = Validation { it }
        fun string(): Validation<String> = Validation { it }
        
        // Common pattern validators
        fun email(): Validation<String> = string().regex(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            "Must be a valid email address"
        )
        
        fun url(): Validation<String> = string().regex(
            "https?://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?",
            "Must be a valid URL"
        )
        
        fun uuid(): Validation<String> = string().regex(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}",
            "Must be a valid UUID"
        )
    }
}

// Int validations
fun Validation<Int>.min(min: Int, message: String = "Must be greater than or equal to $min") = Validation<Int> {
    val it = validate(it)
    if (it < min) add(message)
    it
}

fun Validation<Int>.max(max: Int, message: String = "Must be less than or equal to $max") = Validation<Int> {
    val it = validate(it)
    if (it > max) add(message)
    it
}

fun Validation<Int>.range(min: Int, max: Int, message: String = "Must be between $min and $max") = Validation<Int> {
    val it = validate(it)
    if (it < min || it > max) add(message)
    it
}

@JvmName("intPositive")
fun Validation<Int>.positive(message: String = "Must be positive") = min(1, message)
@JvmName("intNonNegative")
fun Validation<Int>.nonNegative(message: String = "Must be non-negative") = min(0, message)
@JvmName("intNegative")
fun Validation<Int>.negative(message: String = "Must be negative") = max(-1, message)

// Long validations
fun Validation<Long>.min(min: Long, message: String = "Must be greater than or equal to $min") = Validation<Long> {
    val it = validate(it)
    if (it < min) add(message)
    it
}

fun Validation<Long>.max(max: Long, message: String = "Must be less than or equal to $max") = Validation<Long> {
    val it = validate(it)
    if (it > max) add(message)
    it
}

fun Validation<Long>.range(min: Long, max: Long, message: String = "Must be between $min and $max") = Validation<Long> {
    val it = validate(it)
    if (it < min || it > max) add(message)
    it
}

@JvmName("longPositive")
fun Validation<Long>.positive(message: String = "Must be positive") = min(1L, message)
@JvmName("longNonNegative")
fun Validation<Long>.nonNegative(message: String = "Must be non-negative") = min(0L, message)
@JvmName("longNegative")
fun Validation<Long>.negative(message: String = "Must be negative") = max(-1L, message)

// String validations
fun Validation<String>.minLength(min: Int, message: String = "Must have at least $min characters") = Validation<String> {
    val it = validate(it)
    if (it.length < min) add(message)
    it
}

fun Validation<String>.maxLength(max: Int, message: String = "Must have at most $max characters") = Validation<String> {
    val it = validate(it)
    if (it.length > max) add(message)
    it
}

fun Validation<String>.length(min: Int, max: Int, message: String = "Must have between $min and $max characters") = Validation<String> {
    val it = validate(it)
    if (it.length < min || it.length > max) add(message)
    it
}

fun Validation<String>.exactLength(length: Int, message: String = "Must have exactly $length characters") = Validation<String> {
    val it = validate(it)
    if (it.length != length) add(message)
    it
}

@JvmName("stringNotEmpty")
fun Validation<String>.notEmpty(message: String = "Must not be empty") = Validation<String> {
    val it = validate(it)
    if (it.isEmpty()) add(message)
    it
}

fun Validation<String>.notBlank(message: String = "Must not be blank") = Validation<String> {
    val it = validate(it)
    if (it.isBlank()) add(message)
    it
}

fun Validation<String>.regex(pattern: String, message: String = "Must match pattern: $pattern") = Validation<String> {
    val it = validate(it)
    if (!it.matches(Regex(pattern))) add(message)
    it
}

// Collection validations
fun <T> Validation<Collection<T>>.minSize(min: Int, message: String = "Must have at least $min items") = Validation<Collection<T>> {
    val it = validate(it)
    if (it.size < min) add(message)
    it
}

fun <T> Validation<Collection<T>>.maxSize(max: Int, message: String = "Must have at most $max items") = Validation<Collection<T>> {
    val it = validate(it)
    if (it.size > max) add(message)
    it
}

@JvmName("collectionNotEmpty")
fun <T> Validation<Collection<T>>.notEmpty(message: String = "Must not be empty") = Validation<Collection<T>> {
    val it = validate(it)
    if (it.isEmpty()) add(message)
    it
}