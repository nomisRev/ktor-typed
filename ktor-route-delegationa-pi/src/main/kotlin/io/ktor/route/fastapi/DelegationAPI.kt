import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.routing.*
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

data class Example<T>(val value: T, val serializer: KSerializer<T>)

inline fun <reified T> example(value: T) = Example(value, serializer<T>())

fun <T> example(value: T, serializer: KSerializer<T>) = Example(value, serializer)

data class Info<T>(
    val title: String? = null,
    val description: String? = null,
    val example: Example<T>? = null,
    val regex: String? = null,
    val deprecated: Boolean = false,
)

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

class PathDelegate<V>(
    private val name: String?,
    private val typeInfo: TypeInfo,
    private val call: RoutingCall,
    private val validation: Validation<V>?
) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        val paramName = name ?: property.name
        return getParameter(call.pathParameters.getAll(paramName), paramName, typeInfo, validation, mutableListOf())
    }
}

class QueryDelegate<V>(
    private val name: String?,
    private val typeInfo: TypeInfo,
    private val call: RoutingCall,
    private val validation: Validation<V>?
) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        val paramName = name ?: property.name
        // TODO mutableListOf should accumulate values
        return getParameter(call.queryParameters.getAll(paramName), paramName, typeInfo, validation, mutableListOf())
    }
}

/**
 * Delegate for header parameters with validation and metadata support.
 */
class HeaderDelegate<V>(
    private val name: String?,
    private val typeInfo: TypeInfo,
    private val call: RoutingCall,
    private val validation: Validation<V>?,
    private val convertUnderscores: Boolean = true
) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        val paramName = name ?: if (convertUnderscores) {
            property.name.replace('_', '-').split('-').joinToString("-") {
                it.replaceFirstChar { char -> char.uppercaseChar() }
            }
        } else {
            property.name
        }
        return getParameter(call.request.headers.getAll(paramName), paramName, typeInfo, validation, mutableListOf())
    }
}

private fun <V> getParameter(
    values: List<String>?,
    name: String,
    typeInfo: TypeInfo,
    validation: Validation<V>?,
    builder: ValidationBuilder
): V {
    return if (values == null && typeInfo.kotlinType?.isMarkedNullable == true) return null as V
    else if (values == null) throw MissingRequestParameterException(name)
    else try {
        @Suppress("UNCHECKED_CAST")
        val value = DefaultConversionService.fromValues(values, typeInfo) as V
        if (validation != null) builder.validateParameter(value, validation, name)
        value
    } catch (cause: Exception) {
        throw ParameterConversionException(
            name,
            typeInfo.type.simpleName ?: typeInfo.type.toString(),
            cause
        )
    }
}

private fun <T> ValidationBuilder.validateParameter(value: T, validation: Validation<T>, parameterName: String): T {
    val validatedValue = with(validation) { validate(value) }
    return if (isNotEmpty()) throw BadRequestException("Parameter '$parameterName' validation failed: ${joinToString()}")
    else validatedValue
}


inline fun <reified V> RoutingContext.path(
    name: String? = null,
    validation: Validation<V>? = null,
) = PathDelegate(
    name,
    typeInfo<V>(),
    call,
    validation
)

inline fun <reified V> RoutingContext.query(
    name: String? = null,
    validation: Validation<V>? = null,
) = QueryDelegate(
    name,
    typeInfo<V>(),
    call,
    validation
)

// Header parameter functions
inline fun <reified V> RoutingContext.header(
    name: String? = null,
    validation: Validation<V>? = null,
    convertUnderscores: Boolean = true
) = HeaderDelegate(
    name,
    typeInfo<V>(),
    call,
    validation,
    convertUnderscores
)

