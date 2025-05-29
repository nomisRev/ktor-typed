package com.example.test

import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.instanceOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.primaryConstructor
import kotlin.test.Test

annotation class Header(val name: String)

@Serializable
data class CreateUser(
    @Header("X-User-Id") val userId: String,
    val name: String,
    val age: Int

)

inline fun <reified A : Any> Route.trout(
    route: String,
    crossinline block: suspend RoutingContext.(A) -> Unit
) = get(route) {
    val kclass = A::class
    val constructor = kclass.primaryConstructor
    val values = constructor!!.parameters.map { parameter ->
        val header = parameter.annotations.filterIsInstance<Header>().firstOrNull()
        if (header != null) {
            val name = header.name
            val all = parameter.type.isSubtypeOf(Iterable::class.createType(listOf(KTypeProjection.STAR)))
            if (all) call.request.headers.getAll(name) else call.request.headers[name]
        } else {
            call.parameters[parameter.name!!]?.let { convertPrimitives(parameter.type, it) }
        }
    }
    block(constructor.call(*values.toTypedArray()))
}

fun convertPrimitives(klass: KType, value: String) = when  {
    klass.instanceOf(Int::class) -> value.toInt()
    klass.instanceOf(Float::class) -> value.toFloat()
    klass.instanceOf(Double::class) -> value.toDouble()
    klass.instanceOf(Long::class) -> value.toLong()
    klass.instanceOf(Short::class) -> value.toShort()
    klass.instanceOf(Char::class) -> value.single()
    klass.instanceOf(Boolean::class) -> value.toBoolean()
    klass.instanceOf(String::class) -> value
    else -> null
}

class MyTest {
    @Test
    fun test() = testApplication {
//        application {
//            install(ServerContentNegotiation) { json() }
//        }
        routing {
            install(ServerContentNegotiation) { json() }
            trout<CreateUser>("/users/{userId}/create") {
                println(it)
                call.respond(HttpStatusCode.OK, it)
            }
        }
        createClient {
            install(ContentNegotiation) { json() }
        }.get("/users/123/create?name=John&age=30") {
            headers.append("X-User-Id", "123")
        }.body<CreateUser>()
    }
}