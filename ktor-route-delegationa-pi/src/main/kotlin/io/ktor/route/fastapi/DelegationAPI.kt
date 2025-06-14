import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.routing.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * The DSL context that provides access to request data.
 * It's the receiver for the user's lambda in fastPut.
 * @param T The type of the request body.
 */
class PathDelegate<V : Any>(
    private val type: KClass<V>,
    private val call: ApplicationCall,
) : ReadOnlyProperty<Any?, V> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V {
        val value = call.parameters[property.name]
            ?: throw BadRequestException("Missing path parameter: ${property.name}")
        return try {
            convert(value, type)
        } catch (e: NumberFormatException) {
            throw BadRequestException("Cannot convert path parameter '${property.name}' value '$value' to $type", e)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Unsupported type for path parameter '${property.name}': $type", e)
        }
    }
}

class QueryDelegate<V>(
    private val name: String?,
    private val type: KClass<*>,
    private val call: ApplicationCall,
) : ReadOnlyProperty<Any?, V?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V? {
        val paramName = name ?: property.name
        val value = call.request.queryParameters[paramName]
        return value?.let { 
            try {
                convert(it, type) as V
            } catch (e: NumberFormatException) {
                throw BadRequestException("Cannot convert query parameter '$paramName' value '$it' to $type", e)
            } catch (e: IllegalArgumentException) {
                throw BadRequestException("Unsupported type for query parameter '$paramName': $type", e)
            }
        }
    }
}

class HeaderDelegate<V>(
    private val name: String?,
    private val type: KClass<*>,
    private val call: ApplicationCall,
) : ReadOnlyProperty<Any?, V?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): V? {
        val headerName = name ?: property.name
        val value = call.request.headers[headerName]
        return value?.let { 
            try {
                convert(it, type) as V
            } catch (e: NumberFormatException) {
                throw BadRequestException("Cannot convert header '$headerName' value '$it' to $type", e)
            } catch (e: IllegalArgumentException) {
                throw BadRequestException("Unsupported type for header '$headerName': $type", e)
            }
        }
    }
}

inline fun <reified V : Any> RoutingContext.path() = path(V::class)
fun <V : Any> RoutingContext.path(type: KClass<V>) = PathDelegate(type, call)

inline fun <reified V> RoutingContext.query(name: String? = null) = query<V>(V::class, name)
fun <V> RoutingContext.query(type: KClass<*>, name: String? = null) = QueryDelegate<V>(name, type, call)

inline fun <reified V> RoutingContext.header(name: String? = null) = header<V>(V::class, name)
fun <V> RoutingContext.header(type: KClass<*>, name: String? = null) = HeaderDelegate<V>(name, type, call)

/**
 * A basic type converter. A real library would need a more robust solution.
 */
fun <T : Any> convert(value: String, toType: KClass<T>): T {
    val result = when (toType) {
        String::class -> value
        Int::class -> value.toIntOrNull()
        Long::class -> value.toLongOrNull()
        Double::class -> value.toDoubleOrNull()
        Float::class -> value.toFloatOrNull()
        Boolean::class -> value.toBoolean()
        else -> throw IllegalArgumentException("Unsupported type: $toType")
    } ?: throw NumberFormatException("Cannot convert '$value' to $toType")

    @Suppress("UNCHECKED_CAST")
    return result as T
}
