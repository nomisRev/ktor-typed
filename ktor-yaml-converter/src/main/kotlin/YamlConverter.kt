package io.ktor.route.simple

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import io.ktor.client.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.http.ContentType
import io.ktor.serialization.Configuration
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.serialization as serialization
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig as ServerContentNegotiationConfig

// Create a default YAML configuration that handles complex types better
private val defaultYamlConfig = YamlConfiguration(
    strictMode = false,
    polymorphismStyle = PolymorphismStyle.Property,
    encodeDefaults = true
)

// Create a default YAML instance with the improved configuration
private val defaultYaml = Yaml(
    configuration = defaultYamlConfig
)

fun Configuration.yaml(
    yaml: Yaml = defaultYaml,
    contentType: ContentType = ContentType.Application.Yaml,
) {
    serialization(contentType, yaml)
}

fun ContentNegotiationConfig.yaml(
    yaml: Yaml = defaultYaml,
) {
    register(ContentType.Application.Yaml, KotlinxSerializationConverter(yaml))
}

fun ServerContentNegotiationConfig.yaml(
    yaml: Yaml = defaultYaml,
) {
    register(ContentType.Application.Yaml, KotlinxSerializationConverter(yaml))
}
