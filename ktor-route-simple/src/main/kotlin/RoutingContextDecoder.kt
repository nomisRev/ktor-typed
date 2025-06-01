@file:OptIn(ExperimentalSerializationApi::class)

package io.ktor.route.simple

import io.ktor.http.HttpMethod
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.ContentTransformationException
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import io.ktor.util.internal.initCauseBridge
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.CopyableThrowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
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

inline fun <reified A : Any> Route.get(
    path: String,
    noinline block: suspend RoutingContext.(A) -> Unit
) = get(
    path,
    A::class,
    serializer<A>(),
    block
)

fun <A : Any> Route.get(
    path: String,
    kClass: KClass<A>,
    serializer: KSerializer<A>,
    block: suspend RoutingContext.(A) -> Unit
) = route(path, HttpMethod.Get) {
    val bodies = kClass.members.filter { it.annotations.filterIsInstance<Body>().isNotEmpty() }
    val typeInfo = bodies.firstOrNull()?.run { TypeInfo(returnType.classifier as KClass<*>, returnType) }

    require(bodies.size <= 1) { "Only a single or no @Body annotation is allowed but found ${bodies.joinToString { "'${it.name}'" }} in ${kClass.simpleName}" }

    handle {
        val body: Any? = when {
            typeInfo == null -> null
            typeInfo.kotlinType?.isMarkedNullable == true -> try {
                call.receiveNullable<Any?>(typeInfo)
            } catch (_: ContentTransformationException) {
                // TODO how can we distinct between incorrect JSON and null value??
                // Throws ContentTransformationException on null value..
                null
            }

            else -> try {
                call.pipelineCall.receiveNullable<Any?>(typeInfo)!!
            } catch (e: ContentTransformationException) {
                throw BadRequestException("Failed to parse request body", e)
            }
        }
        val value = RoutingContextDecoder(this, serializer.descriptor, body)
            .decodeSerializableValue(serializer)
        block(value)
    }
}

private class RoutingContextDecoder(
    val routingContext: RoutingContext,
    val original: SerialDescriptor,
    val body: Any?,
    override val serializersModule: SerializersModule = EmptySerializersModule()
) : AbstractDecoder() {

    private var index = 0
    private var current: String? = null
    private var headerName: String? = null
    private var isBody: Boolean = false

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val currentIndex = index++
        return if (currentIndex >= original.elementsCount) DECODE_DONE
        else {
            val annotations: List<Annotation> = original.getElementAnnotations(currentIndex)
            headerName = annotations.filterIsInstance<Header>().firstOrNull()?.name
            isBody = annotations.filterIsInstance<Body>().firstOrNull() != null

            if (!isBody) {
                current = when (val headerName = headerName) {
                    null -> routingContext.call.parameters[original.getElementName(currentIndex)]
                    else -> routingContext.call.request.headers[headerName]
                }
            }

            currentIndex
        }
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        val enumName = decodeString()
        val index = enumDescriptor.getElementIndex(enumName)
        if (index == CompositeDecoder.UNKNOWN_NAME) {
            throw BadRequestException("${enumDescriptor.serialName} does not contain element with name '$enumName'")
        }
        return index
    }

    override fun decodeString(): String = current ?: when (val headerName = headerName) {
        null -> throw BadRequestException("Missing parameter '${original.getElementName(index - 1)}'")
        else -> throw MissingHeaderException(headerName)
    }

    override fun decodeBoolean(): Boolean = decodeString().toBoolean()

    override fun decodeByte(): Byte = decodeString().toByte()

    override fun decodeChar(): Char = decodeString().single()

    override fun decodeDouble(): Double = decodeString().toDouble()

    override fun decodeFloat(): Float = decodeString().toFloat()

    override fun decodeInt(): Int = decodeString().toInt()

    override fun decodeLong(): Long = decodeString().toLong()

    override fun decodeShort(): Short = decodeString().toShort()

    override fun decodeNotNullMark(): Boolean =
        !(current == null && original.getElementDescriptor(index - 1).isNullable)

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
        if (isBody) body as T else super.decodeSerializableValue(deserializer)

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return if (descriptor.kind == StructureKind.LIST) {
            val currentIndex = index - 1
            val values = when (val headerName = headerName) {
                null -> routingContext.call.parameters.getAll(original.getElementName(currentIndex))
                else -> routingContext.call.request.headers.getAll(headerName)
            }

            if (values == null && !descriptor.isNullable && !original.isElementOptional(currentIndex)) {
                throw BadRequestException("Missing parameter '${original.getElementName(currentIndex)}'")
            } else {
                ListLikeDecoder(serializersModule, values)
            }
        } else super.beginStructure(descriptor)
    }
}

@OptIn(ExperimentalSerializationApi::class)
private class ListLikeDecoder(
    override val serializersModule: SerializersModule,
    private val values: List<String>?
) : AbstractDecoder() {

    private var currentIndex = -1
    private val elementsCount = values?.size ?: 0

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        if (++currentIndex == elementsCount) DECODE_DONE
        else currentIndex

    override fun decodeBoolean(): Boolean = decodeString().toBoolean()

    override fun decodeByte(): Byte = decodeString().toByte()

    override fun decodeChar(): Char = decodeString().single()

    override fun decodeDouble(): Double = decodeString().toDouble()

    override fun decodeFloat(): Float = decodeString().toFloat()

    override fun decodeInt(): Int = decodeString().toInt()

    override fun decodeLong(): Long = decodeString().toLong()

    override fun decodeShort(): Short = decodeString().toShort()

    override fun decodeString(): String = values!![currentIndex]

    override fun decodeNotNullMark(): Boolean = values != null

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = elementsCount

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        val enumName = decodeString()
        val index = enumDescriptor.getElementIndex(enumName)
        if (index == CompositeDecoder.UNKNOWN_NAME) {
            throw BadRequestException("${enumDescriptor.serialName} does not contain element with name '$enumName'")
        }
        return index
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private class MissingHeaderException(val headerName: String) :
    BadRequestException("Request header $headerName is missing"),
    CopyableThrowable<MissingHeaderException> {

    override fun createCopy(): MissingHeaderException = MissingHeaderException(headerName).also {
        it.initCauseBridge(this)
    }
}
