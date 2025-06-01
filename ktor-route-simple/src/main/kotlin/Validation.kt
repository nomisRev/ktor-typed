package io.ktor.route.simple

import io.ktor.server.plugins.BadRequestException
import kotlin.reflect.KProperty1

class ValidationBuilder<T> {
    private val validators = mutableListOf<Pair<KProperty1<T, *>, (value: Any?) -> String?>>()

    fun validate(instance: T): List<String> =
        validators.mapNotNull { (property, validation: (Any?) -> String?) ->
            validation(property.get(instance))
        }

    fun <R> KProperty1<T, R>.validate(message: String, predicate: (R) -> Boolean): KProperty1<T, R> =
        also { validators.add(Pair(this) { value -> if (predicate(value as R)) null else message }) }

    fun KProperty1<T, String>.notBlank(message: String = "Value must not be blank") =
        validate(message) { it.isNotBlank() }

    fun KProperty1<T, String>.minLength(
        length: Int,
        message: String = "Value must be at least $length characters long"
    ) = validate(message) { it.length >= length }

    fun KProperty1<T, String>.maxLength(
        length: Int,
        message: String = "Value must be at most $length characters long"
    ) = validate(message) { it.length <= length }

    fun KProperty1<T, String>.matches(
        regex: Regex,
        message: String = "Value must match the pattern"
    ) = validate(message) { it.matches(regex) }

    /**
     * Number-specific extension functions for KProperty1.
     */
    fun <R : Comparable<R>> KProperty1<T, R>.min(
        min: R,
        message: String = "Value must be at least $min"
    ) = validate(message) { it >= min }

    fun <R : Comparable<R>> KProperty1<T, R>.max(
        max: R,
        message: String = "Value must be at most $max"
    ) = validate(message) { it <= max }

    /**
     * Collection-specific extension functions for KProperty1.
     */
    fun <R : Collection<*>> KProperty1<T, R>.notEmpty(message: String = "Collection must not be empty") =
        validate(message) { it.isNotEmpty() }

    fun <R : Collection<*>> KProperty1<T, R>.minSize(
        size: Int,
        message: String = "Collection must have at least $size elements"
    ) = validate(message) { it.size >= size }

    fun <R : Collection<*>> KProperty1<T, R>.maxSize(
        size: Int,
        message: String = "Collection must have at most $size elements"
    ) = validate(message) { it.size <= size }
}

/**
 * Main entry point for creating a validation for a data class.
 */
inline fun <reified T : Any> validate(crossinline block: ValidationBuilder<T>.() -> Unit): (T) -> List<String> {
    val validator = ValidationBuilder<T>().apply(block)
    return { instance -> validator.validate(instance) }
}

/**
 * Extension function to apply validation to a value.
 * This can be used in route handlers to validate incoming data.
 */
fun <A : Any> A.validate(validation: (A) -> List<String>): A {
    val errors = validation(this)
    if (errors.isEmpty()) {
        return this
    } else {
        throw BadRequestException("Validation failed: ${errors.joinToString("; ")}")
    }
}

/**
 * Example usage with the fluent DSL:
 *
 * ```
 * val userValidation = validate<User> {
 *     User::id.notBlank().minLength(3)
 *     User::name.notBlank()
 *     User::age.min(18).max(120)
 *     User::tags.notEmpty()
 * }
 *
 * get<User>("/users") { user ->
 *     // Apply validation - throws BadRequestException if validation fails
 *     user.validate(userValidation)
 *
 *     // Process the validated user
 *     call.respond(user)
 *
 *     // Alternatively, you can check validation manually:
 *     // val errors = userValidation(user)
 *     // if (errors.isEmpty()) {
 *     //    call.respond(user)
 *     // } else {
 *     //    call.respond(HttpStatusCode.BadRequest, errors)
 *     // }
 * }
 * ```
 */
