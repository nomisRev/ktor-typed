package io.ktor.route.fastapi

/**
 * Sealed interface representing input parameters for routing functions.
 * Provides type-safe parameter definitions with validation and metadata.
 */
sealed interface Input<A>

/**
 * Represents a path parameter with validation and metadata.
 * Similar to FastAPI's Path(...) function.
 */
data class Path<T>(
    val default: T? = null,
    val title: String? = null,
    val description: String? = null,
    val ge: Number? = null, // greater than or equal
    val gt: Number? = null, // greater than
    val le: Number? = null, // less than or equal
    val lt: Number? = null, // less than
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val regex: String? = null
) : Input<T> {
    companion object {
        /**
         * Creates a required path parameter (equivalent to FastAPI's Path(...))
         */
        fun <T> required(
            title: String? = null,
            description: String? = null,
            ge: Number? = null,
            gt: Number? = null,
            le: Number? = null,
            lt: Number? = null,
            minLength: Int? = null,
            maxLength: Int? = null,
            regex: String? = null
        ): Path<T> = Path(
            default = null,
            title = title,
            description = description,
            ge = ge,
            gt = gt,
            le = le,
            lt = lt,
            minLength = minLength,
            maxLength = maxLength,
            regex = regex
        )
    }
}

/**
 * Represents a query parameter with validation and metadata.
 * Similar to FastAPI's Query(...) function.
 */
data class Query<T>(
    val default: T? = null,
    val title: String? = null,
    val description: String? = null,
    val ge: Number? = null,
    val gt: Number? = null,
    val le: Number? = null,
    val lt: Number? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val regex: String? = null,
    val deprecated: Boolean = false,
    val name: String? = null
) : Input<T> {
    companion object {
        /**
         * Creates a required query parameter (equivalent to FastAPI's Query(...))
         */
        fun <T> required(
            title: String? = null,
            description: String? = null,
            ge: Number? = null,
            gt: Number? = null,
            le: Number? = null,
            lt: Number? = null,
            minLength: Int? = null,
            maxLength: Int? = null,
            regex: String? = null,
            deprecated: Boolean = false,
            name: String? = null
        ): Query<T> = Query(
            default = null,
            title = title,
            description = description,
            ge = ge,
            gt = gt,
            le = le,
            lt = lt,
            minLength = minLength,
            maxLength = maxLength,
            regex = regex,
            deprecated = deprecated,
            name = name
        )
    }
}

/**
 * Represents a header parameter with validation and metadata.
 * Similar to FastAPI's Header(...) function.
 */
data class Header<T>(
    val default: T? = null,
    val title: String? = null,
    val description: String? = null,
    val ge: Number? = null,
    val gt: Number? = null,
    val le: Number? = null,
    val lt: Number? = null,
    val minLength: Int? = null,
    val maxLength: Int? = null,
    val regex: String? = null,
    val deprecated: Boolean = false,
    val convertUnderscores: Boolean = true, // Convert underscores to hyphens in header names
    val name: String? = null
) : Input<T> {
    companion object {
        /**
         * Creates a required header parameter (equivalent to FastAPI's Header(...))
         */
        fun <T> required(
            title: String? = null,
            description: String? = null,
            ge: Number? = null,
            gt: Number? = null,
            le: Number? = null,
            lt: Number? = null,
            minLength: Int? = null,
            maxLength: Int? = null,
            regex: String? = null,
            deprecated: Boolean = false,
            convertUnderscores: Boolean = true,
            name: String? = null
        ): Header<T> = Header(
            default = null,
            title = title,
            description = description,
            ge = ge,
            gt = gt,
            le = le,
            lt = lt,
            minLength = minLength,
            maxLength = maxLength,
            regex = regex,
            deprecated = deprecated,
            convertUnderscores = convertUnderscores,
            name = name
        )
    }
}

/**
 * Represents a request body parameter.
 * Used for complex types that should be parsed from the request body.
 */
data class Body<T>(
    val description: String? = null
) : Input<T> {
    companion object {
        /**
         * Creates a body parameter for request body parsing.
         */
        fun <T> create(description: String? = null): Body<T> = Body(description)
    }
}