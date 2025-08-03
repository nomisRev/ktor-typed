package io.github.nomisrev.typedapi.compiler.plugin.ir

import io.github.nomisrev.typedapi.compiler.plugin.Function2
import io.github.nomisrev.typedapi.compiler.plugin.Pair
import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.StandardClassIds

class IrSymbols(
    ctx: IrPluginContext,
    module: PluginContext,
) {
    private val callableIds = module.callableIds

    // fun <T> arrayOf(vararg elements: T): Array<T>
    val arrayOf: IrSimpleFunctionSymbol = ctx.referenceFunctions(callableIds.arrayOf).single {
        it.owner.typeParameters.size == 1 && it.owner.parameters.size == 1 && it.owner.parameters[0].isVararg
    }

    val stringToNullableAny = ctx.referenceClass(StandardClassIds.Pair)?.let {
        val stringType = ctx.symbols.string.defaultType
        val anyNullableType = ctx.symbols.any.typeWith().makeNullable()
        it.typeWith(listOf(stringType, anyNullableType))
    } ?: error("Couldn't find Pair class")

    val mapEndpoint = ctx.clazz(module.classIds.mapEndpoint)
    val to: IrSimpleFunctionSymbol = ctx.function(callableIds.to)
    val query = ctx.function(callableIds.query)
    val path = ctx.function(callableIds.path)
    val header = ctx.function(callableIds.header)
    val body = ctx.function(callableIds.body)
    val function2 = ctx.referenceClass(StandardClassIds.Function2)
}

private fun IrPluginContext.function(callableId: CallableId): IrSimpleFunctionSymbol =
    referenceFunctions(callableId).singleOrNull()
        ?: error("Couldn't find $callableId function")

private fun IrPluginContext.clazz(classId: ClassId) =
    referenceClass(classId) ?: error("Couldn't find $classId function")