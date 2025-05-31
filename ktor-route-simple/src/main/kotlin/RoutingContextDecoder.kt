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
    var structureFieldIndex = 0
    var descriptor: SerialDescriptor? = null
    var kind: SerialKind? = null

    private var isDecodingList = false
    private var listValues: List<String>? = null
    private var annotation: List<Annotation>? = null
    private var listElementIndex = 0
    private var currentElementName: String? = null

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (isDecodingList) {
            if (listValues != null && listElementIndex < listValues!!.size) {
                val idxToDecode = listElementIndex
                listElementIndex++
                return idxToDecode
            } else {
                return CompositeDecoder.DECODE_DONE
            }
        } else {
            this.descriptor = descriptor
            if (structureFieldIndex >= descriptor.elementsCount) {
                return CompositeDecoder.DECODE_DONE
            }
            currentElementName = descriptor.getElementName(structureFieldIndex)
            annotation = original.getElementAnnotations(structureFieldIndex).ifEmpty { null }
            val idxToDecode = structureFieldIndex
            structureFieldIndex++
            return idxToDecode
        }


    }

    fun name() =
        original.getElementName(structureFieldIndex - 1)

    override fun decodeString(): String =
        decode { it }

    override fun decodeBoolean(): Boolean =
        decode(String::toBooleanStrict)

    override fun decodeByte(): Byte =
        decode(String::toByte)

    override fun decodeShort(): Short =
        decode(String::toShort)

    override fun decodeInt(): Int =
        decode(String::toInt)

    override fun decodeNotNullMark(): Boolean {
        println("decodeNotNullMark: $currentElementName")
        return !original.getElementDescriptor(structureFieldIndex - 1).isNullable
//        return super.decodeNotNullMark()
    }

    override fun decodeNull(): Nothing? {
        println("decodeNull: $currentElementName")
        return super.decodeNull()
    }

    override fun <T : Any> decodeNullableSerializableValue(deserializer: DeserializationStrategy<T?>): T? {
        println("decodeNullableSerializableValue: $deserializer")
        return super.decodeNullableSerializableValue(deserializer)
    }

    private inline fun <reified A> decode(transform: (String) -> A): A {
        println("decode${A::class.simpleName}: $currentElementName, annotation: $annotation, body: $body")
        val elementIndex = structureFieldIndex - 1

        val headerAnnotation = original.getElementAnnotations(elementIndex)
            .filterIsInstance<Header>()
            .firstOrNull()

        val value = when {
            headerAnnotation != null -> routingContext.call.request.headers[headerAnnotation.name]
                ?: throw MissingHeaderException(headerAnnotation.name)

            isDecodingList && listValues != null -> {
                val index = listElementIndex - 1
                if (index < 0 || index >= listValues!!.size) {
                    throw IllegalStateException("Invalid list element index: $index (size: ${listValues!!.size})")
                }
                listValues!![index]
            }

            else -> routingContext.call.parameters.getOrFail(name())
        }

        return transform(value)
    }

    override fun decodeLong(): Long =
        decode(String::toLong)

    override fun decodeFloat(): Float =
        decode(String::toFloat)

    override fun decodeDouble(): Double =
        decode(String::toDouble)

    override fun decodeChar(): Char =
        decode(String::single)

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        println("beginStructure: $descriptor")
        kind = descriptor.kind

        if (descriptor.kind == StructureKind.LIST) {
            isDecodingList = true
            val paramName = name()
            listValues = routingContext.call.parameters.getAll(paramName)
            listElementIndex = 0
        } else {
            isDecodingList = false
            listValues = null
        }

        return super.beginStructure(descriptor)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        println("endStructure: $descriptor")
        if (descriptor.kind == StructureKind.LIST && isDecodingList) {
            isDecodingList = false
            listValues = null
        }
        super.endStructure(descriptor)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int =
        if (isDecodingList && listValues != null) {
            listValues!!.size
        } else {
            super.decodeCollectionSize(descriptor)
        }

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        println("decodeSerializableValue: $deserializer")
        val elementIndex = structureFieldIndex - 1
        if (elementIndex >= 0) {
            val bodyAnnotation = original.getElementAnnotations(elementIndex)
                .filterIsInstance<Body>()
                .firstOrNull()

            if (bodyAnnotation != null) {
                return body as T
            }
        }

        return super.decodeSerializableValue(deserializer)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MissingHeaderException(val headerName: String) : BadRequestException("Request header $headerName is missing"),
    CopyableThrowable<MissingHeaderException> {

    override fun createCopy(): MissingHeaderException = MissingHeaderException(headerName).also {
        it.initCauseBridge(this)
    }
}
