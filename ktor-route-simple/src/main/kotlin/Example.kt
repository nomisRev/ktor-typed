package io.ktor.route.simple

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.util.getOrFail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class JsonBody(val value: String)

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
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        routing {
            install(ContentNegotiation) { json() }
            get("/users/{userId}/create") {
                val value = try {
                    RoutingContextDecoder(this)
                        .decodeSerializableValue(CreateUser.serializer())
                } catch (e: Throwable) {
                    e.printStackTrace()
                    throw e
                }
                println(value)
                call.respond(HttpStatusCode.OK, value)
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

class RoutingContextDecoder(
    val routingContext: RoutingContext,
    override val serializersModule: SerializersModule = EmptySerializersModule()
) : AbstractDecoder() {
    var index = 0
    var descriptor: SerialDescriptor? = null
    var kind: SerialKind? = null

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        println("decodeElementIndex $descriptor: $index")
        this.descriptor = descriptor
        return index++
    }

    fun name() =
        descriptor!!.getElementName(0)

    override fun decodeString(): String {
        println("decodeString")
        return routingContext.call.parameters.getOrFail(name())
    }

    override fun decodeBoolean(): Boolean {
        println("decodeBoolean")
        return routingContext.call.parameters.getOrFail<Boolean>(name())
    }

    override fun decodeByte(): Byte {
        println("decodeByte")
        return routingContext.call.parameters.getOrFail<Byte>(name())
    }

    override fun decodeShort(): Short {
        println("decodeShort")
        return routingContext.call.parameters.getOrFail<Short>(name())
    }

    override fun decodeInt(): Int {
        println("decodeInt")
        return routingContext.call.parameters.getOrFail<Int>(name())
    }

    override fun decodeLong(): Long {
        println("decodeLong")
        return routingContext.call.parameters.getOrFail<Long>(name())
    }

    override fun decodeFloat(): Float {
        println("decodeFloat")
        return routingContext.call.parameters.getOrFail<Float>(name())
    }

    override fun decodeDouble(): Double {
        println("decodeDouble")
        return routingContext.call.parameters.getOrFail<Double>(name())
    }

    override fun decodeChar(): Char {
        println("decodeChar")
        return routingContext.call.parameters.getOrFail<Char>(name())
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
        println("beginStructureL $descriptor")
        kind = descriptor.kind
        return super.beginStructure(descriptor)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        println("endStructure")
        super.endStructure(descriptor)
    }

    override fun decodeSequentially(): Boolean {
        println("decodeSequentially")
        return super.decodeSequentially()
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        println("decodeCollectionSize")
        return super.decodeCollectionSize(descriptor)
    }
}