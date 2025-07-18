package io.github.nomisrev.openapi

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KeepGeneratedSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/** Each Media Type Object provides schema and examples for the media type identified by its key. */
@Serializable(MediaType.Companion.Serializer::class)
@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
public data class MediaType(
  /** The schema defining the content of the request, response, or parameter. */
  public val schema: ReferenceOr<Schema>? = null,
  /**
   * Example of the media type. The example object SHOULD be in the correct format as specified by
   * the media type. The example field is mutually exclusive of the examples field. Furthermore, if
   * referencing a schema which contains an example, the example value SHALL override the example
   * provided by the schema.
   */
  public val example: ExampleValue? = null,
  /**
   * Examples of the media type. Each example object SHOULD match the media type and specified
   * schema if present. The examples field is mutually exclusive of the example field. Furthermore,
   * if referencing a schema which contains an example, the examples value SHALL override the
   * example provided by the schema.
   */
  public val examples: Map<String, ReferenceOr<Example>> = emptyMap(),
  /**
   * A map between a property name and its encoding information. The key, being the property name,
   * MUST exist in the schema as a property. The encoding object SHALL only apply to requestBody
   * objects when the media type is multipart or application/x-www-form-urlencoded.
   */
  public val encoding: Map<String, Encoding> = emptyMap(),
  /**
   * Any additional external documentation for this OpenAPI document. The key is the name of the
   * extension (beginning with x-), and the value is the data. The value can be a [JsonNull],
   * [JsonPrimitive], [JsonArray] or [JsonObject].
   */
  public val extensions: Map<String, JsonElement> = emptyMap(),
) {
  public companion object {
    internal object Serializer :
      KSerializerWithExtensions<MediaType>(
        generatedSerializer(),
        MediaType::extensions,
        { op, extensions -> op.copy(extensions = extensions) },
      )
  }
}
