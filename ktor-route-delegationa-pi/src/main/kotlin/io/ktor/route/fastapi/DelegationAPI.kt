import io.ktor.route.fastapi.Info
import io.ktor.route.fastapi.Validation
import io.ktor.route.fastapi.ValidationBuilder
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.routing.*
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PathDelegate<V>(
    private val name: String?,
    private val typeInfo: TypeInfo,
    private val call: RoutingCall,
    private val validation: Validation<V>?,
    private val info: Info<V>?,
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
    private val validation: Validation<V>?,
    private val info: Info<V>?,
) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        val paramName = name ?: property.name
        // TODO mutableListOf should accumulate values
        return getParameter(call.queryParameters.getAll(paramName), paramName, typeInfo, validation, mutableListOf())
    }
}

class HeaderDelegate<V>(
    private val name: String?,
    private val typeInfo: TypeInfo,
    private val call: RoutingCall,
    private val validation: Validation<V>?,
    private val info: Info<V>?,
    private val convertUnderscores: Boolean
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
): V = when {
    values == null && typeInfo.kotlinType?.isMarkedNullable == true -> null as V
    values == null -> throw MissingRequestParameterException(name)
    else -> {
        val value: Any? = try {
            @Suppress("UNCHECKED_CAST")
            DefaultConversionService.fromValues(values, typeInfo)
        } catch (cause: Exception) {
            throw ParameterConversionException(name, typeInfo.type.simpleName ?: typeInfo.type.toString(), cause)
        }
        if (validation != null) builder.validateParameter(value, validation as Validation<Any?>, name)
        value as V
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
    info: Info<V>? = null,
) = PathDelegate(
    name,
    typeInfo<V>(),
    call,
    validation,
    info
)

inline fun <reified V> RoutingContext.query(
    name: String? = null,
    validation: Validation<V>? = null,
    info: Info<V>? = null,
) = QueryDelegate(
    name,
    typeInfo<V>(),
    call,
    validation,
    info
)

inline fun <reified V> RoutingContext.header(
    name: String? = null,
    validation: Validation<V>? = null,
    info: Info<V>? = null,
    convertUnderscores: Boolean = true
) = HeaderDelegate(
    name,
    typeInfo<V>(),
    call,
    validation,
    info,
    convertUnderscores
)
