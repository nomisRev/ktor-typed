// RUN_PIPELINE_TILL: FRONTEND
// RENDER_DIAGNOSTICS_FULL_TEXT
// DIAGNOSTICS: +REDECLARATION

package my.test

import io.github.nomisrev.typedapi.Endpoint
import io.github.nomisrev.typedapi.EndpointAPI
import io.github.nomisrev.typedapi.query

@Endpoint("/api")
class RedeclaredEndpoint(api: EndpointAPI) {
    val <!REDECLARATION!>name<!> by api.query<String>()
    val <!REDECLARATION!>name<!> by api.query<String>()
}

fun box(): String {
    val value = RedeclaredEndpoint("test", "test")
    return "OK"
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, nullableType, override, primaryConstructor,
propertyDeclaration, propertyDelegate, stringLiteral */
