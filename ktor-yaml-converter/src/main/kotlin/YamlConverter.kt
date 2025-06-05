package io.ktor.route.simple

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.ktor.client.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.http.ContentType
import io.ktor.serialization.Configuration
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig as ServerContentNegotiationConfig
import io.ktor.serialization.kotlinx.serialization as serialization

private val YamlDefault = Yaml(
    configuration = YamlConfiguration(
        strictMode = false,
        encodeDefaults = true,
    )
)

fun Configuration.yaml(
    yaml: Yaml = Yaml.default,
    contentType: ContentType = ContentType.Application.Yaml,
) {
    serialization(contentType, yaml)
}

fun ContentNegotiationConfig.yaml(
    yaml: Yaml = YamlDefault
) {
    register(ContentType.Application.Yaml, KotlinxSerializationConverter(yaml))
}

fun ServerContentNegotiationConfig.yaml(
    yaml: Yaml = YamlDefault
) {
    register(ContentType.Application.Yaml, KotlinxSerializationConverter(yaml))
}
