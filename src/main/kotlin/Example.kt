package com.example

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JsonBody(val value: String)

@Serializable
data class CreateUser(
    val userId: String,
    val name: String,
    val age: Int,
    val many: List<String>,
    val header: Boolean,
    val body: JsonBody
)

val route =
    Path { "users" / string("userId") / "create" }
        .string("name")
        .int("age")
        .header("X-flag")
        .body(JsonBody.serializer())
        .handle<CreateUser> { create ->
            println("I am creating $create")
            call.respond(HttpStatusCode.OK, create)
        }