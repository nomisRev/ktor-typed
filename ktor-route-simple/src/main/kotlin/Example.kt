package io.ktor.route.simple

import io.ktor.http.HttpMethod
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import io.ktor.util.reflect.TypeInfo
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
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

inline fun <reified A : Any> Route.test(
    path: String,
    crossinline block: suspend RoutingContext.(A) -> Unit
) = route(HttpMethod.Get, path, block)

inline fun <reified A : Any> Route.route(
    method: HttpMethod,
    path: String,
    crossinline block: suspend RoutingContext.(A) -> Unit
) = route(path, method) {
    val serializer = serializer<A>()
    val kClass = A::class
    val typeInfo = kClass.members
        .firstOrNull { it.annotations.any { it is Body } }
        ?.run { TypeInfo(returnType.classifier as KClass<*>, returnType) }

    handle {
        val body = if (typeInfo != null) call.receiveNullable<Any?>(typeInfo) else null
        val value = RoutingContextDecoder(
            this,
            serializer.descriptor,
            body
        ).decodeSerializableValue(serializer)
        block(value)
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

    override fun decodeString(): String {
        return if (isDecodingList && listValues != null) {
            val index = listElementIndex - 1
            if (index < 0 || index >= listValues!!.size) {
                throw IllegalStateException("Invalid list element index: $index (size: ${listValues!!.size})")
            }
            listValues!![index]
        } else {
            routingContext.call.parameters.getOrFail(name())
        }
    }

    override fun decodeBoolean(): Boolean {
        val paramName = name()
        val elementIndex = structureFieldIndex - 1

        val headerAnnotation = original.getElementAnnotations(elementIndex)
            .filterIsInstance<Header>()
            .firstOrNull()

        val value = if (headerAnnotation != null) {
            routingContext.call.request.headers[headerAnnotation.name]
                ?: throw MissingHeaderException(headerAnnotation.name)
        } else {
            routingContext.call.parameters.getOrFail(paramName)
        }

        return value.toBooleanStrictOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Boolean")
    }

    override fun decodeByte(): Byte {
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return value.toByteOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Byte")
    }

    override fun decodeShort(): Short {
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return value.toShortOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Short")
    }

    override fun decodeInt(): Int {
        if (isDecodingList && listValues != null) {
            val index = listElementIndex - 1
            if (index < 0 || index >= listValues!!.size) {
                throw IllegalStateException("Invalid list element index: $index (size: ${listValues!!.size})")
            }
            val stringValue = listValues!![index]
            return stringValue.toIntOrNull()
                ?: throw NumberFormatException("Cannot parse list element '$stringValue' as Int")
        } else {
            val paramName = name()
            val value = routingContext.call.parameters.getOrFail(paramName)
            return value.toIntOrNull()
                ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Int")
        }
    }

    override fun decodeLong(): Long {
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return value.toLongOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Long")
    }

    override fun decodeFloat(): Float {
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return value.toFloatOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Float")
    }

    override fun decodeDouble(): Double {
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return value.toDoubleOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Double")
    }

    override fun decodeChar(): Char {
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return if (value.length == 1) value[0] else throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Char")
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
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
