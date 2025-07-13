package io.github.nomisrev.typedapi.compiler.plugin

import io.github.nomisrev.typedapi.Inspectable
import org.jetbrains.kotlin.fir.extensions.AnnotationFqn
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

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
    val inspectable: ClassId = ClassId(
        FqName("io.github.nomisrev.typedapi"),
        Name.identifier("Inspectable"),
    )
)
