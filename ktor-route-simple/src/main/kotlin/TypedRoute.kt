package io.ktor.route.simple

import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.util.internal.initCauseBridge
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.CopyableThrowable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

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

inline fun <reified A : Any> Route.route(
    route: String,
    noinline block: suspend RoutingContext.(A) -> Unit
) = route(route, A::class, block)

@OptIn(InternalSerializationApi::class)
fun <A : Any> Route.route(
    route: String,
    kClass: KClass<A>,
    block: suspend RoutingContext.(A) -> Unit
) {
    val descriptor = kClass.serializer().descriptor
    val constructor = kClass.primaryConstructor!!
    val properties = constructor.parameters

    val bodies = properties.filter { it.annotations.filterIsInstance<Body>().isNotEmpty() }
    require(bodies.size <= 1) { "Only a single or no @Body annotation is allowed but found ${bodies.joinToString { "'${it.name!!}'" }} in ${kClass.simpleName}" }

    get(route) {
        val values: Array<Any?> = Array(descriptor.elementsCount) { index ->
            val property = properties[index]
            // getElementDescriptor lose their @SerialName name, so we need to retrieve it explicitly
            val name = descriptor.getElementName(index)
            val descriptor = descriptor.getElementDescriptor(index)
            val header = property.annotations.filterIsInstance<Header>().singleOrNull()
            val body = property.annotations.filterIsInstance<Body>().singleOrNull()

            val value =
                when {
                    body != null -> {

                        val typeInfo = TypeInfo(property.type.classifier as KClass<*>, property.type)
                        if (descriptor.isNullable) call.receiveNullable(typeInfo) else call.receive(typeInfo)
                    }

                    header != null -> {
                        val value = call.request.headers[header.name]
                        if (value == null && !descriptor.isNullable) throw MissingHeaderException(header.name)
                        value?.convertType(descriptor)
                    }

                    else -> {
                        val value = if (descriptor.kind is StructureKind.LIST) {
                            call.parameters.getAll(name)
                                ?.map { it.convertType(descriptor.elementDescriptors.first()) }
                            // TODO make configurable? Annotation, or plugin setting? both?
                                ?: if (property.isOptional) emptyList<Nothing>() else null
                        } else {
                            call.parameters[name]?.convertType(descriptor)
                        }

                        if (value == null && !descriptor.isNullable)
                            throw MissingRequestParameterException("Parameter $name is required for property ${property.name} in ${kClass.simpleName} but was not found in the request parameters.")

                        value
                    }
                }
            value

            // TODO if you remove x: Any? here the compiler returns Unit, possible only when using `descriptor.kind`.
            //   When function is inlined
//            value?.convertType(kind)
        }

        val input = constructor.call(*values)

        block(input)
    }
}

private fun String.convertType(kind: SerialDescriptor): Any {
    val kind = kind.kind
    require(kind is PrimitiveKind) { "Only supporting primitive types for now. Found $kind." }
    return when (kind) {
        PrimitiveKind.BOOLEAN -> toBooleanStrict()
        PrimitiveKind.BYTE -> toByte()
        PrimitiveKind.CHAR -> single()
        PrimitiveKind.DOUBLE -> toDouble()
        PrimitiveKind.FLOAT -> toFloat()
        PrimitiveKind.INT -> toInt()
        PrimitiveKind.LONG -> toLong()
        PrimitiveKind.SHORT -> toShort()
        PrimitiveKind.STRING -> this
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MissingHeaderException(val headerName: String) : BadRequestException("Request header $headerName is missing"),
    CopyableThrowable<MissingHeaderException> {

    override fun createCopy(): MissingHeaderException = MissingHeaderException(headerName).also {
        it.initCauseBridge(this)
    }
}
