package io.github.nomisrev.typedapi.compiler.plugin

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
    )
)
