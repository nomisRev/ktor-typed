package com.example.test

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import io.ktor.util.reflect.TypeInfo
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType
import kotlin.test.Test
import kotlin.test.assertEquals

annotation class Header(val name: String)

/**
 * Automatically picks up which serializer you're using.
 * It relies on the configured `ContentNegotiation` plugin.
 *
 * Using `receive` and `receiveNullable` on server side.
 */
annotation class Body

@Serializable
data class CreateUser(
    val userId: String,
    @SerialName("name") val NAME: String,
    val age: Int,
    val many: List<String>,
    @Header("X-flag")
    val header: Boolean,
    @Body val body: JsonBody
)

@Serializable
data class JsonBody(val value: String)

@OptIn(InternalSerializationApi::class)
fun <A : Any> Route.trout2(
    route: String,
    kClass: KClass<A>,
    block: suspend RoutingContext.(A) -> Unit
) {
    val descriptor = kClass.serializer().descriptor
    val properties = kClass.primaryConstructor!!.parameters
    // elementDescriptors lose their SerialName naming
    val propertiesDescriptors = descriptor.elementNames.zip(descriptor.elementDescriptors).map { (name, descriptor) ->
        Pair(name, descriptor)
    }

    // Validation logic
    val bodies = propertiesDescriptors.filter { it.second.annotations.filterIsInstance<Body>().size > 1 }
    require(bodies.size <= 1) { "Only a single or no @Body annotation is allowed but found ${bodies.joinToString { it.first }} in ${kClass.simpleName}}" }
    // End Validation logic

    get(route) {
        val values: List<Any?> =
            properties.zip(propertiesDescriptors) { property, (name, descriptor) ->
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
                            if (value == null && !descriptor.isNullable)
                                throw IllegalStateException("Header ${header.name} is required for property ${property.name} in ${kClass.simpleName} but was not found in the request headers.")
                            value?.convertType(descriptor)
                        }

                        else -> {
                            val value = if (descriptor.kind is StructureKind.LIST) {
                                call.parameters.getAll(name)
                                    ?.map { it.convertType(descriptor.elementDescriptors.first()) }
                                    ?: if (property.isOptional) emptyList<Nothing>() else null
                            } else {
                                call.parameters[name]?.convertType(descriptor)
                            }

                            if (value == null && !descriptor.isNullable)
                                throw IllegalStateException("Parameter $name is required for property ${property.name} in ${kClass.simpleName} but was not found in the request parameters.")
                            value
                        }
                    }
                value

                // TODO if you remove x: Any? here the compiler returns Unit, possible only when using `descriptor.kind`.
                //   When function is inlined
//            value?.convertType(kind)
            }

        val input = kClass.primaryConstructor!!.call(*values.toTypedArray())

        block(input)
    }
}

private suspend fun <T : Any> ApplicationCall.receiveNullable(type: KClass<T>): T? {
    val kotlinType = type.starProjectedType
    return receiveNullable(TypeInfo(type, kotlinType))
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

class MyTest {
    @Test
    fun test() = testApplication {
        routing {
            install(ServerContentNegotiation) { json() }
            trout2("/users/{userId}/create", CreateUser::class) {
                println(it)
                call.respond(HttpStatusCode.OK, it)
            }
        }
        val user = createClient {
            install(ContentNegotiation) { json() }
        }.get("/users/123/create?name=John&age=30") {
            headers.append("X-flag", "true")
            url.parameters.appendAll("many", listOf("a", "b", "c"))
            contentType(ContentType.Application.Json)
            setBody(JsonBody("Hello World"))
        }.body<CreateUser>()
        assertEquals(
            CreateUser(
                userId = "123",
                NAME = "John",
                age = 30,
                many = listOf("a", "b", "c"),
                header = true,
                body = JsonBody("Hello World")
            ),
            user
        )
    }
}