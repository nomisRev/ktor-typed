package io.ktor.route.simple

import io.ktor.server.plugins.BadRequestException
import kotlin.reflect.KProperty1

typealias Validation<T> = (value: T) -> String?

/**
 * Creates a validation function from a predicate function.
 */
fun <T> Validation(message: String, predicate: (T) -> Boolean): Validation<T> =
    { value -> if (predicate(value)) null else message }

/**
 * Represents a validation rule for a property of type T.
 */
class PropertyValidator<T, R>(
    private val property: KProperty1<T, R>,
    private val validations: List<Validation<R>>
) {
    fun validate(instance: T): List<String> {
        val value = property.get(instance)
        return validations.mapNotNull { validation ->
            validation(value) // Apply validation function and filter out nulls
        }
    }
}

/**
 * Builder for creating validation rules for a data class.
 */
class ValidationBuilder<T> {
    private val validators = mutableListOf<PropertyValidator<T, *>>()

    /**
     * Define validation rules for a property.
     */
    fun <R> property(
        property: KProperty1<T, R>,
        block: PropertyValidationBuilder<R>.() -> Unit
    ) {
        val builder = PropertyValidationBuilder<R>()
        builder.block()
        validators.add(PropertyValidator(property, builder.validations))
    }

    /**
     * Register a property validator created with the fluent API.
     */
    fun <R> register(validator: PropertyValidator<T, R>) {
        validators.add(validator)
    }

    /**
     * Validate an instance of the data class.
     * @return ValidationResult containing the result of the validation.
     *         An empty list means validation passed, a non-empty list contains error messages.
     */
    fun validate(instance: T): List<String> {
        return validators.flatMap { it.validate(instance) }
    }

    /**
     * Extension functions for KProperty1 to enable the fluent DSL syntax.
     */
    fun <R> KProperty1<T, R>.validationDsl(): PropertyValidationDsl<T, R> {
        val dsl = PropertyValidationDsl(this, builder = this@ValidationBuilder)
        return dsl
    }

    /**
     * String-specific extension functions for KProperty1.
     */
    fun KProperty1<T, String>.notBlank(message: String = "Value must not be blank"): PropertyValidationDsl<T, String> {
        val dsl = PropertyValidationDsl(this, builder = this@ValidationBuilder).notBlank(message)
        register(dsl.toValidator())
        return dsl
    }

    fun KProperty1<T, String>.minLength(
        length: Int,
        message: String = "Value must be at least $length characters long"
    ): PropertyValidationDsl<T, String> {
        val dsl = PropertyValidationDsl(this, builder = this@ValidationBuilder).minLength(length, message)
        register(dsl.toValidator())
        return dsl
    }

    fun KProperty1<T, String>.maxLength(
        length: Int,
        message: String = "Value must be at most $length characters long"
    ): PropertyValidationDsl<T, String> {
        val dsl = PropertyValidationDsl(this, builder = this@ValidationBuilder).maxLength(length, message)
        register(dsl.toValidator())
        return dsl
    }

    fun KProperty1<T, String>.matches(
        regex: Regex,
        message: String = "Value must match the pattern"
    ): PropertyValidationDsl<T, String> {
        val dsl = PropertyValidationDsl(this, builder = this@ValidationBuilder).matches(regex, message)
        register(dsl.toValidator())
        return dsl
    }

    /**
     * Number-specific extension functions for KProperty1.
     */
    fun <R : Comparable<R>> KProperty1<T, R>.min(
        min: R,
        message: String = "Value must be at least $min"
    ): PropertyValidationDsl<T, R> {
        val dsl = PropertyValidationDsl(this, builder = this@ValidationBuilder).min(min, message)
        register(dsl.toValidator())
        return dsl
    }

    fun <R : Comparable<R>> KProperty1<T, R>.max(
        max: R,
        message: String = "Value must be at most $max"
    ): PropertyValidationDsl<T, R> {
        val dsl = PropertyValidationDsl(this, builder = this@ValidationBuilder).max(max, message)
        register(dsl.toValidator())
        return dsl
    }

    /**
     * Collection-specific extension functions for KProperty1.
     */
    fun <R : Collection<*>> KProperty1<T, R>.notEmpty(message: String = "Collection must not be empty"): PropertyValidationDsl<T, R> {
        val dsl = PropertyValidationDsl(this, builder = this@ValidationBuilder).notEmpty(message)
        register(dsl.toValidator())
        return dsl
    }

    fun <R : Collection<*>> KProperty1<T, R>.minSize(
        size: Int,
        message: String = "Collection must have at least $size elements"
    ): PropertyValidationDsl<T, R> {
        val dsl = PropertyValidationDsl(this, builder = this@ValidationBuilder).minSize(size, message)
        register(dsl.toValidator())
        return dsl
    }

    fun <R : Collection<*>> KProperty1<T, R>.maxSize(
        size: Int,
        message: String = "Collection must have at most $size elements"
    ): PropertyValidationDsl<T, R> {
        val dsl = PropertyValidationDsl(this, builder = this@ValidationBuilder).maxSize(size, message)
        register(dsl.toValidator())
        return dsl
    }
}

/**
 * Builder for creating validation rules for a property.
 */
class PropertyValidationBuilder<T> {
    val validations = mutableListOf<Validation<T>>()

    /**
     * Add a custom validation rule.
     */
    fun validate(message: String, predicate: (T) -> Boolean) {
        validations.add { value -> if (predicate(value)) null else message }
    }

    /**
     * Validate that a value is not null.
     */
    fun notNull(message: String = "Value must not be null") {
        validations.add(Validation(message) { it != null })
    }
}

/**
 * Class for fluent property validation DSL.
 */
class PropertyValidationDsl<T, R>(
    private val property: KProperty1<T, R>,
    private val validations: MutableList<Validation<R>> = mutableListOf(),
    private val builder: ValidationBuilder<T>? = null
) {
    /**
     * Add a custom validation rule.
     */
    fun validate(message: String, predicate: (R) -> Boolean): PropertyValidationDsl<T, R> {
        validations.add { value -> if (predicate(value)) null else message }
        return this
    }

    /**
     * Create a PropertyValidator from this DSL.
     */
    internal fun toValidator(): PropertyValidator<T, R> {
        return PropertyValidator(property, validations)
    }

    /**
     * Register this validator with the builder.
     */
    internal fun register() {
        builder?.register(toValidator())
    }
}

/**
 * String-specific validation extensions for PropertyValidationBuilder.
 */
fun PropertyValidationBuilder<String>.notBlank(message: String = "Value must not be blank") {
    validate(message) { it.isNotBlank() }
}

fun PropertyValidationBuilder<String>.minLength(
    length: Int,
    message: String = "Value must be at least $length characters long"
) {
    validate(message) { it.length >= length }
}

fun PropertyValidationBuilder<String>.maxLength(
    length: Int,
    message: String = "Value must be at most $length characters long"
) {
    validate(message) { it.length <= length }
}

fun PropertyValidationBuilder<String>.matches(regex: Regex, message: String = "Value must match the pattern") {
    validate(message) { it.matches(regex) }
}

/**
 * String-specific validation extensions for fluent DSL.
 */
fun <T> PropertyValidationDsl<T, String>.notBlank(message: String = "Value must not be blank"): PropertyValidationDsl<T, String> {
    return validate(message) { it.isNotBlank() }
}

fun <T> PropertyValidationDsl<T, String>.minLength(
    length: Int,
    message: String = "Value must be at least $length characters long"
): PropertyValidationDsl<T, String> {
    return validate(message) { it.length >= length }
}

fun <T> PropertyValidationDsl<T, String>.maxLength(
    length: Int,
    message: String = "Value must be at most $length characters long"
): PropertyValidationDsl<T, String> {
    return validate(message) { it.length <= length }
}

fun <T> PropertyValidationDsl<T, String>.matches(
    regex: Regex,
    message: String = "Value must match the pattern"
): PropertyValidationDsl<T, String> {
    return validate(message) { it.matches(regex) }
}

/**
 * Number-specific validation extensions for PropertyValidationBuilder.
 */
fun <T : Comparable<T>> PropertyValidationBuilder<T>.min(min: T, message: String = "Value must be at least $min") {
    validate(message) { it >= min }
}

fun <T : Comparable<T>> PropertyValidationBuilder<T>.max(max: T, message: String = "Value must be at most $max") {
    validate(message) { it <= max }
}

/**
 * Number-specific validation extensions for fluent DSL.
 */
fun <T, R : Comparable<R>> PropertyValidationDsl<T, R>.min(
    min: R,
    message: String = "Value must be at least $min"
): PropertyValidationDsl<T, R> {
    return validate(message) { it >= min }
}

fun <T, R : Comparable<R>> PropertyValidationDsl<T, R>.max(
    max: R,
    message: String = "Value must be at most $max"
): PropertyValidationDsl<T, R> {
    return validate(message) { it <= max }
}

/**
 * Collection-specific validation extensions for PropertyValidationBuilder.
 */
fun <T : Collection<*>> PropertyValidationBuilder<T>.notEmpty(message: String = "Collection must not be empty") {
    validate(message) { it.isNotEmpty() }
}

fun <T : Collection<*>> PropertyValidationBuilder<T>.minSize(
    size: Int,
    message: String = "Collection must have at least $size elements"
) {
    validate(message) { it.size >= size }
}

fun <T : Collection<*>> PropertyValidationBuilder<T>.maxSize(
    size: Int,
    message: String = "Collection must have at most $size elements"
) {
    validate(message) { it.size <= size }
}

/**
 * Collection-specific validation extensions for fluent DSL.
 */
fun <T, R : Collection<*>> PropertyValidationDsl<T, R>.notEmpty(message: String = "Collection must not be empty"): PropertyValidationDsl<T, R> {
    return validate(message) { it.isNotEmpty() }
}

fun <T, R : Collection<*>> PropertyValidationDsl<T, R>.minSize(
    size: Int,
    message: String = "Collection must have at least $size elements"
): PropertyValidationDsl<T, R> {
    return validate(message) { it.size >= size }
}

fun <T, R : Collection<*>> PropertyValidationDsl<T, R>.maxSize(
    size: Int,
    message: String = "Collection must have at most $size elements"
): PropertyValidationDsl<T, R> {
    return validate(message) { it.size <= size }
}

/**
 * Main entry point for creating a validation for a data class.
 */
inline fun <reified T : Any> validate(crossinline block: ValidationBuilder<T>.() -> Unit): (T) -> List<String> {
    val builder = ValidationBuilder<T>()
    builder.block()
    return { instance -> builder.validate(instance) }
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
 * Example usage with the new fluent DSL:
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
