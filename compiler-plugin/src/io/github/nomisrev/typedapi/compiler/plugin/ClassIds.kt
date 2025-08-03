package io.github.nomisrev.typedapi.compiler.plugin

import org.jetbrains.kotlin.fir.extensions.AnnotationFqn
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds

class ClassIds(
    val annotation: AnnotationFqn = FqName("io.github.nomisrev.typedapi.Endpoint"),
    val mapEndpoint: ClassId = ClassId(
        FqName("io.github.nomisrev.typedapi"),
        Name.identifier("MapEndpointAPI"),
    ),
    val input: ClassId = ClassId(
        FqName("io.github.nomisrev.typedapi"),
        Name.identifier("Input"),
    ),
    val inputQuery: ClassId = input.createNestedClassId(Name.identifier("Query")),
    val inputHeader: ClassId = input.createNestedClassId(Name.identifier("Header")),
    val inputPath: ClassId = input.createNestedClassId(Name.identifier("Path")),
    val inputBody: ClassId = input.createNestedClassId(Name.identifier("Body")),
    val httpRequestValue: ClassId = ClassId(
        FqName("io.github.nomisrev.typedapi"),
        Name.identifier("HttpRequestValue"),
    )
)


val StandardClassIds.Pair: ClassId
    get() = ClassId.fromString("kotlin/Pair")

val StandardClassIds.Function2: ClassId
    get() = StandardClassIds.FunctionN(2)
