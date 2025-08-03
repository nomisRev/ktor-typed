package io.github.nomisrev.typedapi.compiler.plugin.ir

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.symbols.IrSymbol

internal fun IrGeneratorContext.createIrBuilder(symbol: IrSymbol): DeclarationIrBuilder =
    DeclarationIrBuilder(this, symbol, symbol.owner.startOffset, symbol.owner.endOffset)