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

// Double validations
fun Validation<Double>.min(min: Double, message: String = "Must be greater than or equal to $min") = Validation<Double> {
    val it = validate(it)
    if (it < min) add(message)
    it
}

fun Validation<Double>.max(max: Double, message: String = "Must be less than or equal to $max") = Validation<Double> {
    val it = validate(it)
    if (it > max) add(message)
    it
}

fun Validation<Double>.range(min: Double, max: Double, message: String = "Must be between $min and $max") = Validation<Double> {
    val it = validate(it)
    if (it < min || it > max) add(message)
    it
}

@JvmName("doublePositive")
fun Validation<Double>.positive(message: String = "Must be positive") = min(0.0 + Double.MIN_VALUE, message)
@JvmName("doubleNonNegative")
fun Validation<Double>.nonNegative(message: String = "Must be non-negative") = min(0.0, message)
@JvmName("doubleNegative")
fun Validation<Double>.negative(message: String = "Must be negative") = max(0.0 - Double.MIN_VALUE, message)

// Float validations
fun Validation<Float>.min(min: Float, message: String = "Must be greater than or equal to $min") = Validation<Float> {
    val it = validate(it)
    if (it < min) add(message)
    it
}

fun Validation<Float>.max(max: Float, message: String = "Must be less than or equal to $max") = Validation<Float> {
    val it = validate(it)
    if (it > max) add(message)
    it
}

fun Validation<Float>.range(min: Float, max: Float, message: String = "Must be between $min and $max") = Validation<Float> {
    val it = validate(it)
    if (it < min || it > max) add(message)
    it
}

@JvmName("floatPositive")
fun Validation<Float>.positive(message: String = "Must be positive") = min(0.0f + Float.MIN_VALUE, message)
@JvmName("floatNonNegative")
fun Validation<Float>.nonNegative(message: String = "Must be non-negative") = min(0.0f, message)
@JvmName("floatNegative")
fun Validation<Float>.negative(message: String = "Must be negative") = max(0.0f - Float.MIN_VALUE, message)

// Short validations
fun Validation<Short>.min(min: Short, message: String = "Must be greater than or equal to $min") = Validation<Short> {
    val it = validate(it)
    if (it < min) add(message)
    it
}

fun Validation<Short>.max(max: Short, message: String = "Must be less than or equal to $max") = Validation<Short> {
    val it = validate(it)
    if (it > max) add(message)
    it
}

fun Validation<Short>.range(min: Short, max: Short, message: String = "Must be between $min and $max") = Validation<Short> {
    val it = validate(it)
    if (it < min || it > max) add(message)
    it
}

@JvmName("shortPositive")
fun Validation<Short>.positive(message: String = "Must be positive") = min(1.toShort(), message)
@JvmName("shortNonNegative")
fun Validation<Short>.nonNegative(message: String = "Must be non-negative") = min(0.toShort(), message)
@JvmName("shortNegative")
fun Validation<Short>.negative(message: String = "Must be negative") = max((-1).toShort(), message)

// Byte validations
fun Validation<Byte>.min(min: Byte, message: String = "Must be greater than or equal to $min") = Validation<Byte> {
    val it = validate(it)
    if (it < min) add(message)
    it
}

fun Validation<Byte>.max(max: Byte, message: String = "Must be less than or equal to $max") = Validation<Byte> {
    val it = validate(it)
    if (it > max) add(message)
    it
}

fun Validation<Byte>.range(min: Byte, max: Byte, message: String = "Must be between $min and $max") = Validation<Byte> {
    val it = validate(it)
    if (it < min || it > max) add(message)
    it
}

@JvmName("bytePositive")
fun Validation<Byte>.positive(message: String = "Must be positive") = min(1.toByte(), message)
@JvmName("byteNonNegative")
fun Validation<Byte>.nonNegative(message: String = "Must be non-negative") = min(0.toByte(), message)
@JvmName("byteNegative")
fun Validation<Byte>.negative(message: String = "Must be negative") = max((-1).toByte(), message)

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

fun Validation<String>.contains(substring: String, message: String = "Must contain '$substring'", ignoreCase: Boolean = false) = Validation<String> {
    val it = validate(it)
    if (!it.contains(substring, ignoreCase)) add(message)
    it
}

fun Validation<String>.startsWith(prefix: String, message: String = "Must start with '$prefix'") = Validation<String> {
    val it = validate(it)
    if (!it.startsWith(prefix)) add(message)
    it
}

fun Validation<String>.endsWith(suffix: String, message: String = "Must end with '$suffix'") = Validation<String> {
    val it = validate(it)
    if (!it.endsWith(suffix)) add(message)
    it
}

fun Validation<String>.oneOf(vararg values: String, message: String = "Must be one of: ${values.joinToString()}", ignoreCase: Boolean = false) = Validation<String> {
    val it = validate(it)
    if (!values.any { value -> it.equals(value, ignoreCase) }) add(message)
    it
}

fun Validation<String>.alphanumeric(message: String = "Must contain only letters and digits") = Validation<String> {
    val it = validate(it)
    if (!it.all { char -> char.isLetterOrDigit() }) add(message)
    it
}

fun Validation<String>.alphabetic(message: String = "Must contain only letters") = Validation<String> {
    val it = validate(it)
    if (!it.all { char -> char.isLetter() }) add(message)
    it
}

fun Validation<String>.numeric(message: String = "Must contain only digits") = Validation<String> {
    val it = validate(it)
    if (!it.all { char -> char.isDigit() }) add(message)
    it
}

fun Validation<String>.lowercase(message: String = "Must be lowercase") = Validation<String> {
    val it = validate(it)
    if (it != it.lowercase()) add(message)
    it
}

fun Validation<String>.uppercase(message: String = "Must be uppercase") = Validation<String> {
    val it = validate(it)
    if (it != it.uppercase()) add(message)
    it
}

// Boolean validations
fun Validation<Boolean>.isTrue(message: String = "Must be true") = Validation<Boolean> {
    val it = validate(it)
    if (!it) add(message)
    it
}

fun Validation<Boolean>.isFalse(message: String = "Must be false") = Validation<Boolean> {
    val it = validate(it)
    if (it) add(message)
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

fun <T> Validation<Collection<T>>.size(min: Int, max: Int, message: String = "Must have between $min and $max items") = Validation<Collection<T>> {
    val it = validate(it)
    if (it.size < min || it.size > max) add(message)
    it
}

fun <T> Validation<Collection<T>>.exactSize(size: Int, message: String = "Must have exactly $size items") = Validation<Collection<T>> {
    val it = validate(it)
    if (it.size != size) add(message)
    it
}

@JvmName("collectionNotEmpty")
fun <T> Validation<Collection<T>>.notEmpty(message: String = "Must not be empty") = Validation<Collection<T>> {
    val it = validate(it)
    if (it.isEmpty()) add(message)
    it
}