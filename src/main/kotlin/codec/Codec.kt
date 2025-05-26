package com.example.codec

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.*

interface Codec<A> {
    val serializer: KSerializer<A>

    suspend fun serialize(value: A): String?

    // TODO this should probably not be simple A?, but perhaps not heavy weight Either. Result? or custom..?
    suspend fun deserialize(value: String): A

    companion object {
        
        @JvmStatic
        val int = object : Codec<Int> {
            override val serializer: KSerializer<Int> = Int.serializer()
            override suspend fun serialize(value: Int): String? = value.toString()
            override suspend fun deserialize(value: String): Int = value.toInt()
        }

        @JvmStatic
        val intOrNull = object : Codec<Int?> {
            override val serializer: KSerializer<Int?> = Int.serializer().nullable
            override suspend fun serialize(value: Int?): String? = value?.toString()
            override suspend fun deserialize(value: String): Int? = value.toIntOrNull()
        }

        
        @JvmStatic
        val boolean = object : Codec<Boolean> {
            override val serializer: KSerializer<Boolean> = Boolean.serializer()
            override suspend fun serialize(value: Boolean): String? = value.toString()
            override suspend fun deserialize(value: String): Boolean = value.toBoolean()
        }

        @JvmStatic
        val booleanOrNull = object : Codec<Boolean?> {
            override val serializer: KSerializer<Boolean?> = Boolean.serializer().nullable
            override suspend fun serialize(value: Boolean?): String? = value?.toString()
            override suspend fun deserialize(value: String): Boolean? = value.toBooleanStrictOrNull()
        }

        @JvmStatic
        val byte = object : Codec<Byte> {
            override val serializer: KSerializer<Byte> = Byte.serializer()
            override suspend fun serialize(value: Byte): String? = value.toString()
            override suspend fun deserialize(value: String): Byte = value.toByte()
        }

        @JvmStatic
        val byteOrNull = object : Codec<Byte?> {
            override val serializer: KSerializer<Byte?> = Byte.serializer().nullable
            override suspend fun serialize(value: Byte?): String? = value?.toString()
            override suspend fun deserialize(value: String): Byte? = value.toByteOrNull()
        }

        @JvmStatic
        val short = object : Codec<Short> {
            override val serializer: KSerializer<Short> = Short.serializer()
            override suspend fun serialize(value: Short): String? = value.toString()
            override suspend fun deserialize(value: String): Short = value.toShort()
        }

        @JvmStatic
        val shortOrNull = object : Codec<Short?> {
            override val serializer: KSerializer<Short?> = Short.serializer().nullable
            override suspend fun serialize(value: Short?): String? = value?.toString()
            override suspend fun deserialize(value: String): Short? = value.toShortOrNull()
        }

        @JvmStatic
        val long = object : Codec<Long> {
            override val serializer: KSerializer<Long> = Long.serializer()
            override suspend fun serialize(value: Long): String? = value.toString()
            override suspend fun deserialize(value: String): Long = value.toLong()
        }

        @JvmStatic
        val longOrNull = object : Codec<Long?> {
            override val serializer: KSerializer<Long?> = Long.serializer().nullable
            override suspend fun serialize(value: Long?): String? = value?.toString()
            override suspend fun deserialize(value: String): Long? = value.toLongOrNull()
        }

        @JvmStatic
        val float = object : Codec<Float> {
            override val serializer: KSerializer<Float> = Float.serializer()
            override suspend fun serialize(value: Float): String? = value.toString()
            override suspend fun deserialize(value: String): Float = value.toFloat()
        }

        @JvmStatic
        val floatOrNull = object : Codec<Float?> {
            override val serializer: KSerializer<Float?> = Float.serializer().nullable
            override suspend fun serialize(value: Float?): String? = value?.toString()
            override suspend fun deserialize(value: String): Float? = value.toFloatOrNull()
        }

        @JvmStatic
        val double = object : Codec<Double> {
            override val serializer: KSerializer<Double> = Double.serializer()
            override suspend fun serialize(value: Double): String? = value.toString()
            override suspend fun deserialize(value: String): Double = value.toDouble()
        }

        @JvmStatic
        val doubleOrNull = object : Codec<Double?> {
            override val serializer: KSerializer<Double?> = Double.serializer().nullable
            override suspend fun serialize(value: Double?): String? = value?.toString()
            override suspend fun deserialize(value: String): Double? = value.toDoubleOrNull()
        }

        @JvmStatic
        val string = object : Codec<String> {
            override val serializer: KSerializer<String> = String.serializer()
            override suspend fun serialize(value: String): String? = value
            override suspend fun deserialize(value: String): String = value
        }

        @JvmStatic
        val stringOrNull = object : Codec<String?> {
            override val serializer: KSerializer<String?> = String.serializer().nullable
            override suspend fun serialize(value: String?): String? = value
            override suspend fun deserialize(value: String): String? = value
        }

//        @JvmStatic
//        fun <T> json(serializer: KSerializer<T>, json: Json = Json) = object : Codec<T> {
//            override val serializer: KSerializer<T> = serializer()
//            override suspend fun serialize(value: T): String = json.encodeToString(serializer, value)
//            override suspend fun deserialize(value: String): T = json.decodeFromString(serializer, value)
//        }
    }
}
