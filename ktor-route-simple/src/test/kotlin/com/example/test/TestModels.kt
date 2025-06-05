package com.example.test

import io.ktor.route.simple.Body
import io.ktor.route.simple.Header
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Body(val value: String)

@Serializable
data class CreateUser(
    val userId: String,
    @SerialName("name") val NAME: String,
    val age: Int,
    val many: List<String>,
    @Header("X-flag")
    val header: Boolean,
    @Body val body: com.example.test.Body
)

@Serializable
data class TwoBodies(@Body val one: com.example.test.Body, @Body val two: String)

@Serializable
data class AllPrimitives(
    val stringValue: String,
    val intValue: Int,
    val longValue: Long,
    val doubleValue: Double,
    val floatValue: Float,
    val booleanValue: Boolean,
    val byteValue: Byte,
    val charValue: Char,
    val shortValue: Short,
    val nullableStringValue: String?,
    val nullableIntValue: Int?,
    val nullableLongValue: Long?,
    val nullableDoubleValue: Double?,
    val nullableFloatValue: Float?,
    val nullableBooleanValue: Boolean?,
    val nullableByteValue: Byte?,
    val nullableCharValue: Char?,
    val nullableShortValue: Short?
)

@Serializable
data class ListPrimitives(
    val stringList: List<String>,
    val intList: List<Int>,
    val longList: List<Long>,
    val doubleList: List<Double>,
    val floatList: List<Float>,
    val booleanList: List<Boolean>,
    val byteList: List<Byte>,
    val charList: List<Char>,
    val shortList: List<Short>,
    val nullableStringList: List<String?>,
    val nullableIntList: List<Int?>,
    val nullableLongList: List<Long?>,
    val nullableDoubleList: List<Double?>,
    val nullableFloatList: List<Float?>,
    val nullableBooleanList: List<Boolean?>,
    val nullableByteList: List<Byte?>,
    val nullableCharList: List<Char?>,
    val nullableShortList: List<Short?>
)

@Serializable
data class NullableHeader(
    @Header("X-Nullable-Header") val nullableHeader: String?
)

@Serializable
data class NullableBody(
    @Body val nullableBody: com.example.test.Body?
)