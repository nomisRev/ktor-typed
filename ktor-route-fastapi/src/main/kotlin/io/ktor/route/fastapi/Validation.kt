package io.ktor.route.fastapi

import io.ktor.server.plugins.BadRequestException

/**
 * Validates a value against the constraints defined in a parameter metadata object.
 */
internal fun <T> validateParameter(value: T, metadata: Any, parameterName: String): T {
    when (metadata) {
        is Path<*> -> validateConstraints(value, metadata, parameterName)
        is Query<*> -> validateConstraints(value, metadata, parameterName)
        is Header<*> -> validateConstraints(value, metadata, parameterName)
    }
    return value
}

private fun <T> validateConstraints(value: T, metadata: Any, parameterName: String) {
    val ge = when (metadata) {
        is Path<*> -> metadata.ge
        is Query<*> -> metadata.ge
        is Header<*> -> metadata.ge
        else -> null
    }
    val gt = when (metadata) {
        is Path<*> -> metadata.gt
        is Query<*> -> metadata.gt
        is Header<*> -> metadata.gt
        else -> null
    }
    val le = when (metadata) {
        is Path<*> -> metadata.le
        is Query<*> -> metadata.le
        is Header<*> -> metadata.le
        else -> null
    }
    val lt = when (metadata) {
        is Path<*> -> metadata.lt
        is Query<*> -> metadata.lt
        is Header<*> -> metadata.lt
        else -> null
    }
    val minLength = when (metadata) {
        is Path<*> -> metadata.minLength
        is Query<*> -> metadata.minLength
        is Header<*> -> metadata.minLength
        else -> null
    }
    val maxLength = when (metadata) {
        is Path<*> -> metadata.maxLength
        is Query<*> -> metadata.maxLength
        is Header<*> -> metadata.maxLength
        else -> null
    }
    val regex = when (metadata) {
        is Path<*> -> metadata.regex
        is Query<*> -> metadata.regex
        is Header<*> -> metadata.regex
        else -> null
    }

    // Numeric validations
    if (value is Number) {
        ge?.let { min ->
            if (value.toDouble() < min.toDouble()) {
                throw BadRequestException("Parameter '$parameterName' must be greater than or equal to $min, got $value")
            }
        }
        gt?.let { min ->
            if (value.toDouble() <= min.toDouble()) {
                throw BadRequestException("Parameter '$parameterName' must be greater than $min, got $value")
            }
        }
        le?.let { max ->
            if (value.toDouble() > max.toDouble()) {
                throw BadRequestException("Parameter '$parameterName' must be less than or equal to $max, got $value")
            }
        }
        lt?.let { max ->
            if (value.toDouble() >= max.toDouble()) {
                throw BadRequestException("Parameter '$parameterName' must be less than $max, got $value")
            }
        }
    }

    // String validations
    if (value is String) {
        minLength?.let { min ->
            if (value.length < min) {
                throw BadRequestException("Parameter '$parameterName' must have at least $min characters, got ${value.length}")
            }
        }
        maxLength?.let { max ->
            if (value.length > max) {
                throw BadRequestException("Parameter '$parameterName' must have at most $max characters, got ${value.length}")
            }
        }
        regex?.let { pattern ->
            if (!value.matches(Regex(pattern))) {
                throw BadRequestException("Parameter '$parameterName' does not match the required pattern: $pattern")
            }
        }
    }
}