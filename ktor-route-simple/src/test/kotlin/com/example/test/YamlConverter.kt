package com.example.test

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.ContentConverter
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.serializer

/**
 * Custom content type for YAML
 */
val ContentType.Application.Yaml: ContentType
    get() = ContentType("application", "yaml")

/**
 * Extension function to add YAML content negotiation to the client
 */
fun ContentNegotiation.Configuration.yaml(
    yaml: Yaml = Yaml.default
) {
    register(ContentType.Application.Yaml, YamlConverter(yaml))
}

/**
 * Extension function to add YAML content negotiation to the server
 */
fun ServerContentNegotiation.Configuration.yaml(
    yaml: Yaml = Yaml.default
) {
    register(ContentType.Application.Yaml, YamlConverter(yaml))
}

/**
 * YAML content converter for Ktor
 */
class YamlConverter(private val yaml: Yaml) : ContentConverter {
    override suspend fun serialize(
        contentType: ContentType,
        charset: Charset,
        typeInfo: io.ktor.serialization.TypeInfo,
        value: Any
    ): ByteReadChannel {
        val serializer = serializer(typeInfo.type)
        val text = yaml.encodeToString(serializer, value)
        return ByteReadChannel(text.toByteArray(charset))
    }

    override suspend fun deserialize(
        charset: Charset,
        typeInfo: io.ktor.serialization.TypeInfo,
        content: ByteReadChannel
    ): Any {
        val text = content.readRemaining().readText(charset)
        val serializer = serializer(typeInfo.type)
        return yaml.decodeFromString(serializer, text)
    }
}

/**
 * YAML body class similar to JsonBody
 */
@kotlinx.serialization.Serializable
data class YamlBody(val value: String)