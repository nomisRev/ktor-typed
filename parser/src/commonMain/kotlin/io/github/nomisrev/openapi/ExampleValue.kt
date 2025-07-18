package io.github.nomisrev.openapi

import com.charleskorn.kaml.YamlInput
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlNode
import com.charleskorn.kaml.YamlScalar
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlin.jvm.JvmInline
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

public typealias DefaultValue = ExampleValue

@Serializable(with = ExampleValue.Companion.Serializer::class)
public sealed interface ExampleValue {

    @JvmInline
    public value class Single(public val value: String) : ExampleValue {
        override fun toString(): String = value
    }

    @JvmInline
    public value class Multiple(public val values: List<String>) : ExampleValue {
        override fun toString(): String = values.toString()
    }

    public companion object {

        internal class Serializer : KSerializer<ExampleValue> {
            private val multipleSerializer = ListSerializer(String.serializer())

            // TODO implement proper SerialDescriptor
            @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
            override val descriptor: SerialDescriptor =
                buildSerialDescriptor("io.github.nomisrev.openapi.ExampleValueSerializer", SerialKind.CONTEXTUAL)

            override fun serialize(encoder: Encoder, value: ExampleValue) {
                when (value) {
                    is Single -> encoder.encodeString(value.value)
                    is Multiple -> encoder.encodeSerializableValue(multipleSerializer, value.values)
                }
            }

            override fun deserialize(decoder: Decoder): ExampleValue = when {
                decoder is JsonDecoder -> when (val json = decoder.decodeSerializableValue(JsonElement.serializer())) {
                    is JsonArray -> Multiple(decoder.decodeSerializableValue(multipleSerializer))
                    is JsonPrimitive -> Single(json.content)
                    else ->
                        throw SerializationException(
                            "ExampleValue can only be a primitive or an array, found $json"
                        )
                }

                decoder is YamlInput -> {
                    val node = decoder.decodeSerializableValue(YamlNode.serializer())
                    when {
                        node is YamlList -> Multiple(decoder.decodeSerializableValue(multipleSerializer))
                        node is YamlScalar -> Single(node.content)
                        else -> throw SerializationException("ExampleValue can only be a primitive or an array")
                    }
                }

                else -> error("This $decoder is not supported")
            }
        }

        public operator fun invoke(v: String): ExampleValue = Single(v)
    }
}
