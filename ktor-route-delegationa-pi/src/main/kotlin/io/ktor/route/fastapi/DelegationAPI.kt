import io.ktor.server.application.*
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.routing.*
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.TypeInfo
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.starProjectedType

/**
 * The DSL context that provides access to request data.
 * It's the receiver for the user's lambda in fastPut.
 * @param T The type of the request body.
 */
class PathDelegate<V>(
    private val name: String?,
    private val typeInfo: TypeInfo,
    private val call: ApplicationCall,
) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V =
        getValue(name ?: property.name, typeInfo, call)
}

private fun <V> getValue(
    name: String,
    typeInfo: TypeInfo,
    call: ApplicationCall
): V {
    val values = call.parameters.getAll(name) ?: throw MissingRequestParameterException(name)
    return try {
        @Suppress("UNCHECKED_CAST")
        DefaultConversionService.fromValues(values, typeInfo) as V
    } catch (cause: Exception) {
        throw ParameterConversionException(
            name,
            typeInfo.type.simpleName ?: typeInfo.type.toString(),
            cause
        )
    }
}

class QueryDelegate<V>(
    private val name: String?,
    private val typeInfo: TypeInfo,
    private val call: ApplicationCall,
) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V =
        getValue(name ?: property.name, typeInfo, call)
}

class HeaderDelegate<V>(
    private val name: String?,
    private val typeInfo: TypeInfo,
    private val call: ApplicationCall,
) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V =
        getValue(name ?: property.name, typeInfo, call)
}

inline fun <reified V> RoutingContext.path(name: String? = null) = path<V>(V::class, name)
fun <V> RoutingContext.path(type: KClass<*>, name: String? = null) =
    PathDelegate<V>(name, TypeInfo(type, type.starProjectedType), call)

inline fun <reified V> RoutingContext.query(name: String? = null) = query<V>(V::class, name)
fun <V> RoutingContext.query(type: KClass<*>, name: String? = null) =
    QueryDelegate<V>(name, TypeInfo(type, type.starProjectedType), call)

inline fun <reified V> RoutingContext.header(name: String? = null) = header<V>(V::class, name)
fun <V> RoutingContext.header(type: KClass<*>, name: String? = null) =
    HeaderDelegate<V>(name, TypeInfo(type, type.starProjectedType), call)
