import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

// ===================================================================================
// Part 1: THE "LIBRARY" CODE (What you would publish as a reusable library)
// ===================================================================================

/**
 * The DSL context that provides access to request data.
 * It's the receiver for the user's lambda in fastPut.
 * @param T The type of the request body.
 */
class FastRouteContext<T : Any>(
    private val call: ApplicationCall,
    private val body: T
) {
    // Delegate for reading path parameters.
    inner class PathDelegate<V : Any>(private val type: KClass<V>) : ReadOnlyProperty<Any?, V> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): V {
            val value = call.parameters[property.name]
                ?: throw IllegalArgumentException("Missing path parameter: ${property.name}")
            return convert(value, type)
        }
    }

    // Delegate for reading query parameters.
    inner class QueryDelegate<V>(private val name: String?, private val type: KClass<*>) : ReadOnlyProperty<Any?, V?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): V? {
            val paramName = name ?: property.name
            val value = call.request.queryParameters[paramName]
            return value?.let { convert(it, type) as V }
        }
    }

    // Delegate for reading header parameters.
    inner class HeaderDelegate<V>(private val name: String?, private val type: KClass<*>) : ReadOnlyProperty<Any?, V?> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): V? {
            val headerName = name ?: property.name
            val value = call.request.headers[headerName]
            return value?.let { convert(it, type) as V }
        }
    }

    // Provides a delegate for path parameters.
    fun <V : Any> path(type: KClass<V>) = PathDelegate(type)

    // Provides a delegate for query parameters.
    fun <V> query(type: KClass<*>, name: String? = null) = QueryDelegate<V>(name, type)

    // Provides a delegate for header parameters.
    fun <V> header(type: KClass<*>, name: String? = null) = HeaderDelegate<V>(name, type)

    // Provides access to the pre-parsed request body.
    fun body(): T = body
}

/**
 * A generic, DSL-based PUT route handler that mimics FastAPI's style without annotations.
 *
 * @param T The type of the request body.
 * @param path The route path, e.g., "/items/{id}".
 * @param block The handler block with FastRouteContext as its receiver. The last expression is sent as the response.
 */
inline fun <reified T : Any> Route.fastPut(path: String, noinline block: suspend FastRouteContext<T>.() -> Any) {
    put(path) {
        try {
            // Eagerly receive the body once. This is the key to solving the suspend issue for the DSL.
            val body = call.receive<T>()
            val context = FastRouteContext(call, body)

            // Execute the user's logic within the context.
            val result = context.block()

            // The last expression of the user's block is the result.
            call.respond(HttpStatusCode.OK, result)

        } catch (e: Exception) {
            // Basic error handling for missing params, conversion errors, etc.
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to (e.message ?: "Bad Request")))
        }
    }
}

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

// ===================================================================================
// Part 2: THE "USER" CODE (How you would use the library in your application)
// ===================================================================================

// 2a. The Request Body Data Class (like a Pydantic model)
@Serializable
data class Item(
    val name: String,
    val description: String? = null,
    val price: Double,
    val tax: Double? = null
)

// 2b. The response model
@Serializable
data class UpdateResponse(
    val itemId: Int,
    val query: String?,
    val userAgent: String?,
    val xToken: String?,
    val receivedItem: Item
)

// 2c. Main Ktor server setup
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json()
        }
        routing {
            get("/") {
                call.respondText("Server is running! Try making a PUT request to /items/123?q=myquery")
            }

            // This is the new, annotation-free, DSL-based developer experience.
            fastPut<Item>("/items/{itemId}") {
                // Use delegates to declare expected parameters from the request.
                // The name is inferred from the property name.
                val itemId by path(Int::class)
                val q by query<String>(String::class)
                val userAgent by header<String>(String::class, name = "User-Agent")
                val xToken by header<String>(String::class, name = "X-Token")

                // Get the deserialized body.
                val item = body()

                // The rest of the block is your business logic.
                // Parameter values are accessed here, triggering the delegates.
                UpdateResponse(
                    itemId = itemId,
                    query = q,
                    userAgent = userAgent,
                    xToken = xToken,
                    receivedItem = item
                )
                // The UpdateResponse object is the last expression, so it will be returned automatically.
            }
        }
    }.start(wait = true)
}