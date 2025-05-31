@file:OptIn(ExperimentalSerializationApi::class)

package io.ktor.route.simple

import io.ktor.http.HttpMethod
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import io.ktor.util.internal.initCauseBridge
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.CopyableThrowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

/**
 * Annotation for marking header parameters in route data classes.
 */
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
@ExperimentalSerializationApi
annotation class Header(val name: String)

/**
 * Automatically picks up which serializer you're using.
 * It relies on the configured `ContentNegotiation` plugin.
 *
 * Using `receive` and `receiveNullable` on server side.
 */
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
@ExperimentalSerializationApi
annotation class Body

@Serializable
data class JsonBody(val value: String)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CreateUser(
    val userId: String,
    @SerialName("NAME") val name: String,
    val age: Int,
    val many: List<String>,
    @Header("X-flag")
    val header: Boolean,
    @Body val body: JsonBody
)

// TODO make actual impl non-inline
inline fun <reified A : Any> Route.get(
    path: String,
    crossinline block: suspend RoutingContext.(A) -> Unit
) = route(path, HttpMethod.Get) {
    val serializer = serializer<A>()
    val kClass = A::class
    val bodies = kClass.members
        .filter { it.annotations.filterIsInstance<Body>().isNotEmpty() }
    val typeInfo = bodies.firstOrNull()?.run { TypeInfo(returnType.classifier as KClass<*>, returnType) }

    require(bodies.size <= 1) { "Only a single or no @Body annotation is allowed but found ${bodies.joinToString { "'${it.name}'" }} in ${kClass.simpleName}" }

    handle {
        try {
            val body =
                if (typeInfo != null) call.receiveNullable<Any?>(typeInfo) else null
            val value = RoutingContextDecoder(this, serializer.descriptor, body)
                .decodeSerializableValue(serializer)
            block(value)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }
}

class RoutingContextDecoder(
    val routingContext: RoutingContext,
    val original: SerialDescriptor,
    val body: Any?,
    override val serializersModule: SerializersModule = EmptySerializersModule()
) : AbstractDecoder() {

    var index = 0
    var listIndex = DECODE_DONE
    private var currentDescriptor: SerialDescriptor? = null
    private var current: String? = null
    private var currents: List<String>? = null
    private var header: Header? = null
    private var bodyOrNull: Any? = null

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val currentIndex = index++
        if (currentIndex >= original.elementsCount) return DECODE_DONE
        currentDescriptor = original.getElementDescriptor(currentIndex)
        val annotations: List<Annotation> = original.getElementAnnotations(currentIndex)
        header = annotations.filterIsInstance<Header>().firstOrNull()
        bodyOrNull = annotations.filterIsInstance<Body>().firstOrNull()

        if (
            currentDescriptor?.kind == StructureKind.LIST
            && currents == null
            && listIndex == DECODE_DONE
        ) {
            currents = when {
                header != null -> routingContext.call.request.headers.getAll(header!!.name)
                else -> routingContext.call.parameters.getAll(original.getElementName(currentIndex))
            }
            listIndex = 0
        }

        when {
            bodyOrNull != null -> Unit
            currents != null ->
                if (listIndex >= (currents?.size ?: 0)) {
                    return DECODE_DONE
                } else {
                    current = currents!![listIndex]
                    return listIndex++
                }

            else -> {
                current = when {
                    header != null -> routingContext.call.request.headers[header!!.name]
                    else -> routingContext.call.parameters[original.getElementName(currentIndex)]
                }
            }
        }

        return currentIndex
    }

    // TODO implement ENUM
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return super.decodeEnum(enumDescriptor)
    }

    override fun decodeString(): String =
        currentValue()

    override fun decodeShort(): Short =
        currentValue().toShort()

    override fun decodeLong(): Long =
        currentValue().toLong()

    override fun decodeInt(): Int =
        currentValue().toInt()

    override fun decodeFloat(): Float =
        currentValue().toFloat()

    override fun decodeDouble(): Double =
        currentValue().toDouble()

    override fun decodeChar(): Char =
        currentValue().single()

    override fun decodeByte(): Byte =
        currentValue().toByte()

    override fun decodeBoolean(): Boolean =
        currentValue().toBooleanStrict()

    fun currentValue(): String =
        current ?: when {
            header != null -> throw MissingHeaderException(header!!.name)
            else -> throw BadRequestException("Missing parameter '${original.getElementName(index - 1)}'")
        }

    fun name() =
        original.getElementName(index - 1)

    override fun decodeNotNullMark(): Boolean =
        !(current == null && currentDescriptor?.isNullable == true)

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
        if (bodyOrNull != null) body as T
        else super.decodeSerializableValue(deserializer)

    override fun endStructure(descriptor: SerialDescriptor) {
        if (descriptor.kind == StructureKind.LIST && current != null) {
            currents = null
            listIndex = DECODE_DONE
        }
        super.endStructure(descriptor)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        val size = currents?.size ?: super.decodeCollectionSize(descriptor)
        return size
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MissingHeaderException(val headerName: String) : BadRequestException("Request header $headerName is missing"),
    CopyableThrowable<MissingHeaderException> {

    override fun createCopy(): MissingHeaderException = MissingHeaderException(headerName).also {
        it.initCauseBridge(this)
    }
}
