package io.github.nomisrev.typedapi.compiler.plugin.fir

import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.FirVariable
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.buildResolvedArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildArgumentList
import org.jetbrains.kotlin.fir.expressions.impl.FirResolvedArgumentList
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import kotlin.collections.plusAssign

fun FirCallableSymbol<*>.toResolvedNamedReference(): FirResolvedNamedReference =
    buildResolvedNamedReference {
        this.name = this@toResolvedNamedReference.name
        resolvedSymbol = this@toResolvedNamedReference
    }

fun FirVariable.toResolvedNamedReference(): FirResolvedNamedReference =
    buildResolvedNamedReference {
        this.name = this@toResolvedNamedReference.name
        resolvedSymbol = this@toResolvedNamedReference.symbol
    }
fun Map<out FirExpression, FirValueParameter>.toResolvedArgumentList(): FirResolvedArgumentList {
    val original = buildArgumentList { arguments += keys }
    return buildResolvedArgumentList(original, LinkedHashMap(this))
}
