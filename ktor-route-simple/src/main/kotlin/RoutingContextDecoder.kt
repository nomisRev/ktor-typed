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

// TODO make actual impl non-inline
fun <A : Any> Route.get(
    path: String,
    kClass: KClass<A>,
    serializer: KSerializer<A>,
    block: suspend RoutingContext.(A) -> Unit
) = route(path, HttpMethod.Get) {
    val bodies = kClass.members
        .filter { it.annotations.filterIsInstance<Body>().isNotEmpty() }
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

class RoutingContextDecoder(
    val routingContext: RoutingContext,
    val original: SerialDescriptor,
    val body: Any?,
    override val serializersModule: SerializersModule = EmptySerializersModule()
) : AbstractDecoder() {

    var index = 0
    private var currentDescriptor: SerialDescriptor? = null
    private var current: String? = null
    private var header: Header? = null
    private var bodyOrNull: Any? = null

    fun log(name: String, descriptor: SerialDescriptor?) =
        println(
            """
            $name($descriptor)
            index: $index currentDescriptor: $currentDescriptor current: $current header: $header bodyOrNull: $bodyOrNull
        """.trimIndent()
        )

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        log("decodeElementIndex", descriptor)
        val currentIndex = index++
        return if (currentIndex >= original.elementsCount) DECODE_DONE
        else {
            currentDescriptor = original.getElementDescriptor(currentIndex)
            val annotations: List<Annotation> = original.getElementAnnotations(currentIndex)
            header = annotations.filterIsInstance<Header>().firstOrNull()
            bodyOrNull = annotations.filterIsInstance<Body>().firstOrNull()

            if (bodyOrNull == null) {
                current = when {
                    header != null -> routingContext.call.request.headers[header!!.name]
                    else -> routingContext.call.parameters[original.getElementName(currentIndex)]
                }
            }

            currentIndex
        }
    }

    // TODO implement ENUM
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        log("decodeEnum", enumDescriptor)
        return super.decodeEnum(enumDescriptor)
    }

    override fun decodeString(): String {
        log("decodeString", null)
        return currentValue()
    }

    override fun decodeShort(): Short {
        log("decodeShort", null)
        return currentValue().toShort()
    }

    override fun decodeLong(): Long {
        log("decodeLong", null)
        return currentValue().toLong()
    }

    override fun decodeInt(): Int {
        log("decodeLong", null)
        return currentValue().toInt()
    }

    override fun decodeFloat(): Float {
        log("decodeFloat", null)
        return currentValue().toFloat()
    }

    override fun decodeDouble(): Double {
        log("decodeDouble", null)
        return currentValue().toDouble()
    }

    override fun decodeChar(): Char {
        log("decodeChar", null)
        return currentValue().single()
    }

    override fun decodeByte(): Byte {
        log("decodeByte", null)
        return currentValue().toByte()
    }

    override fun decodeBoolean(): Boolean {
        log("decodeBoolean", null)
        return currentValue().toBooleanStrict()
    }

    fun currentValue(): String =
        current ?: when {
            header != null -> throw MissingHeaderException(header!!.name)
            else -> throw BadRequestException("Missing parameter '${original.getElementName(index - 1)}'")
        }

    override fun decodeNotNullMark(): Boolean {
        log("decodeNotNullMark", null)
        return !(current == null && currentDescriptor?.isNullable == true)
    }

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        log("decodeSerializableValue", deserializer.descriptor)
        return if (bodyOrNull != null) body as T
        else super.decodeSerializableValue(deserializer)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        log("beginStructure", descriptor)
        return if (descriptor.kind == StructureKind.LIST) {
            val values = when {
                header != null -> routingContext.call.request.headers.getAll(header!!.name)
                else -> routingContext.call.parameters.getAll(original.getElementName(index - 1))
            }

            if (values == null && !descriptor.isNullable && !original.isElementOptional(index - 1))
                throw BadRequestException("Missing parameter '${original.getElementName(index - 1)}'")

            ListLikeDecoder(
                serializersModule,
                original.getElementDescriptor(index - 1),
                original.getElementName(index - 1),
                values
            )
        } else super.beginStructure(descriptor)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        log("endStructure", descriptor)
        super.endStructure(descriptor)
    }
}

@OptIn(ExperimentalSerializationApi::class)
private class ListLikeDecoder(
    override val serializersModule: SerializersModule,
    values1: SerialDescriptor,
    values2: String,
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
class MissingHeaderException(val headerName: String) : BadRequestException("Request header $headerName is missing"),
    CopyableThrowable<MissingHeaderException> {

    override fun createCopy(): MissingHeaderException = MissingHeaderException(headerName).also {
        it.initCauseBridge(this)
    }
}
