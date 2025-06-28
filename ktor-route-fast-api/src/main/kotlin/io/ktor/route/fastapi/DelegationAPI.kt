import io.ktor.route.fastapi.Info
import io.ktor.route.fastapi.Validation
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class Endp<F>(
    val inputs: Array<out Input<*>>,
    val transform: (Array<Any?>) -> F,
    val reverse: (F) -> Array<Any?>
)

fun <E> endpoint(vararg inputs: Input<*>): Endp<E> =
    Endp(inputs, { TODO() }) { TODO() }

sealed interface Input<V> {
    data class Path<V>(
        private val name: String?,
        private val kClass: KClass<*>,
        private val kType: KType,
        private val validation: Validation<V>?,
        private val info: Info<V>?,
    ) : Input<V>

    data class Query<V>(
        private val name: String?,
        private val kClass: KClass<*>,
        private val kType: KType,
        private val validation: Validation<V>?,
        private val info: Info<V>?,
    ) : Input<V>

    data class Header<V>(
        private val name: String?,
        private val kClass: KClass<*>,
        private val kType: KType,
        private val validation: Validation<V>?,
        private val info: Info<V>?,
    ) : Input<V>

    companion object {
        inline fun <reified V> path(
            name: String?,
            validation: Validation<V>? = null,
            info: Info<V>? = null,
        ) = Path(name, V::class, typeOf<V>(), validation, info)

        inline fun <reified V> query(
            name: String?,
            validation: Validation<V>? = null,
            info: Info<V>? = null,
        ) = Query(name, V::class, typeOf<V>(), validation, info)

        inline fun <reified V> header(
            name: String?,
            validation: Validation<V>? = null,
            info: Info<V>? = null,
        ) = Header(name, V::class, typeOf<V>(), validation, info)
    }
}
