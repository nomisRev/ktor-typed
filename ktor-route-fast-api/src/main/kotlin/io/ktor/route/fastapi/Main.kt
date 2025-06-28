package io.ktor.route.fastapi

import Endp
import endpoint
import kotlinx.serialization.Serializable

@Serializable
data class Profile(val name: String, val age: Int, val email: String?, val userAgent: String)

fun main() {
    val serializer = Profile.serializer()
    val descriptor = serializer.descriptor

    val x = Profile::name

    // We'd need to be able to hide these type arguments somehow...
    // We need to implement our own typing... so just <*> ???
    val endp: Endp<Profile> = endpoint(
        Input.path<String>("name"),
        Input.query<Int>("age"),
        Input.query<String?>("email"),
        Input.header<String>("User-Agent")
    )
    println("test")
}