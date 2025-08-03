package io.github.nomisrev.typedapi.compiler.plugin

import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class CallableIds(
    val body: CallableId = CallableId(FqName("io.github.nomisrev.typedapi"), null, Name.identifier("Body")),
    val query: CallableId = CallableId(FqName("io.github.nomisrev.typedapi"), null, Name.identifier("Query")),
    val header: CallableId = CallableId(FqName("io.github.nomisrev.typedapi"), null, Name.identifier("Header")),
    val path: CallableId = CallableId(FqName("io.github.nomisrev.typedapi"), null, Name.identifier("Path")),
    val to: CallableId = CallableId(FqName("kotlin"), null, Name.identifier("to")),
    val arrayOf: CallableId = CallableId(FqName("kotlin"), null, Name.identifier("arrayOf"))
)