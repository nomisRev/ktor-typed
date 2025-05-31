package io.ktor.route.simple

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
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

fun main() {
    val descriptor = CreateUser.serializer().descriptor
//    val annotations = descriptor.annotations()
//    val elementsAnnotations = descriptor.elementNames.mapIndexed { index, _ ->
//        descriptor.getElementDescriptor(index).annotations()
//    }
//    println("descriptor: $descriptor, annotations: $annotations, elementsAnnotations: $elementsAnnotations")
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        routing {
            install(ContentNegotiation) { json() }
            route(
                HttpMethod.Get,
                "/users/{userId}/create"
            ) { create: CreateUser ->
                call.respond(HttpStatusCode.OK, create)
            }
        }
    }.start(wait = true)
}

fun Application.example() = routing {
    route<CreateUser>("/users/{userId}/create") { create ->
        println("I am creating $create")
        call.respond(HttpStatusCode.OK, create)
    }
}

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
        // TODO error handling for missing body
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
    // Index for fields of the current structure being decoded (e.g., fields of CreateUser)
    var structureFieldIndex = 0
    var descriptor: SerialDescriptor? = null
    var kind: SerialKind? = null
    private var isDecodingList = false
    private var listValues: List<String>? = null
    private var annotation: List<Annotation>? = null

    // Index for elements within the current list being decoded
    private var listElementIndex = 0
    private var currentElementName: String? = null

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (isDecodingList) {
            // Decoding elements OF a list
            if (listValues != null && listElementIndex < listValues!!.size) {
                val idxToDecode = listElementIndex
                println("decodeElementIndex (LIST MODE) for ${descriptor.serialName}: returning element index $idxToDecode")
                listElementIndex++
                return idxToDecode
            } else {
                println("decodeElementIndex (LIST MODE) for ${descriptor.serialName}: DECODE_DONE")
                return CompositeDecoder.DECODE_DONE
            }
        } else {
            // Decoding fields of a STRUCTURE
            println("decodeElementIndex (STRUCTURE MODE) for ${descriptor.serialName}: current structureFieldIndex $structureFieldIndex / ${descriptor.elementsCount}")
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
        println("decodeString")
        return if (isDecodingList && listValues != null) {
            // listElementIndex is incremented by decodeElementIndex, so use listElementIndex-1
            val index = listElementIndex - 1
            if (index < 0 || index >= listValues!!.size) {
                throw IllegalStateException("Invalid list element index: $index (size: ${listValues!!.size})")
            }
            println("Decoding string list element at index $index: '${listValues!![index]}'")
            listValues!![index]
        } else {
            routingContext.call.parameters.getOrFail(name())
        }
    }

    override fun decodeBoolean(): Boolean {
        println("decodeBoolean")
        val paramName = name()
        val elementIndex = structureFieldIndex - 1

        // Check for Header annotation
        val headerAnnotation = original.getElementAnnotations(elementIndex)
            .filterIsInstance<Header>()
            .firstOrNull()

        println("Element '$paramName' annotations: ${original.getElementAnnotations(elementIndex)}")

        if (headerAnnotation != null) {
            println("Found @Header annotation with name: ${headerAnnotation.name}")
            val headerValue = routingContext.call.request.headers[headerAnnotation.name]
                ?: throw MissingHeaderException(headerAnnotation.name)
            return when (headerValue.lowercase()) {
                "true" -> true
                "false" -> false
                else -> throw IllegalArgumentException("Cannot convert header ${headerAnnotation.name} with value '$headerValue' to Boolean")
            }
        }

        val value = routingContext.call.parameters.getOrFail(paramName)
        return when (value.lowercase()) {
            "true" -> true
            "false" -> false
            else -> throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Boolean")
        }
    }

    override fun decodeByte(): Byte {
        println("decodeByte")
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return value.toByteOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Byte")
    }

    override fun decodeShort(): Short {
        println("decodeShort")
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return value.toShortOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Short")
    }

    override fun decodeInt(): Int {
        println("decodeInt")
        if (isDecodingList && listValues != null) {
            // listElementIndex is incremented by decodeElementIndex, so use listElementIndex-1
            val index = listElementIndex - 1
            if (index < 0 || index >= listValues!!.size) {
                throw IllegalStateException("Invalid list element index: $index (size: ${listValues!!.size})")
            }
            val stringValue = listValues!![index]
            println("Decoding int list element at index $index: '$stringValue'")
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
        println("decodeLong")
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return value.toLongOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Long")
    }

    override fun decodeFloat(): Float {
        println("decodeFloat")
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return value.toFloatOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Float")
    }

    override fun decodeDouble(): Double {
        println("decodeDouble")
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return value.toDoubleOrNull()
            ?: throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Double")
    }

    override fun decodeChar(): Char {
        println("decodeChar")
        val paramName = name()
        val value = routingContext.call.parameters.getOrFail(paramName)
        return if (value.length == 1) value[0] else throw IllegalArgumentException("Cannot convert parameter $paramName with value '$value' to Char")
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        println("decodeEnum")
        return super.decodeEnum(enumDescriptor)
    }

    override fun decodeNotNullMark(): Boolean {
        println("decodeNotNullMark")
        return super.decodeNotNullMark()
    }

    override fun decodeNull(): Nothing? {
        println("decodeNull")
        return super.decodeNull()
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        println("beginStructure for ${descriptor.serialName} (Kind: ${descriptor.kind})")
        kind = descriptor.kind

        // Check if we're decoding a list
        if (descriptor.kind == StructureKind.LIST) {
            isDecodingList = true
            val paramName = name()
            listValues = routingContext.call.parameters.getAll(paramName) /* ?: emptyList() */
            println("List parameter name: $paramName, values: $listValues")
            listElementIndex = 0
        } else {
            isDecodingList = false
            listValues = null
        }

        return super.beginStructure(descriptor)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        println("endStructure for ${descriptor.serialName} (Kind: ${descriptor.kind})")
        if (descriptor.kind == StructureKind.LIST && isDecodingList) {
            println("endStructure: Resetting list decoding state.")
            isDecodingList = false
            listValues = null
        }
        super.endStructure(descriptor)
    }

    override fun decodeSequentially(): Boolean {
        println("decodeSequentially")
        return super.decodeSequentially()
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        if (isDecodingList && listValues != null) {
            println("decodeCollectionSize (LIST MODE) for ${descriptor.serialName}: size ${listValues!!.size}")
            return listValues!!.size
        } else {
            println("decodeCollectionSize (STRUCTURE MODE) for ${descriptor.serialName}: using super")
            return super.decodeCollectionSize(descriptor)
        }
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
