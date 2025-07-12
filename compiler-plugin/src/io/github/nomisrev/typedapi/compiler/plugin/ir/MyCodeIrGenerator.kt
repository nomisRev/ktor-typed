package io.github.nomisrev.typedapi.compiler.plugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import io.github.nomisrev.typedapi.compiler.plugin.fir.MyCodeGenerationExtension
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irConstantPrimitive
import org.jetbrains.kotlin.ir.builders.irDelegatingConstructorCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irSetField
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFactory
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstantValue
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.toIrConst
import org.jetbrains.kotlin.ir.visitors.IrVisitor
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class MyCodeIrGenerationExtension(private val module: PluginContext) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        moduleFragment.accept(MyCodeIrGenerator(pluginContext, module), null)
    }
}

internal fun IrGeneratorContext.createIrBuilder(symbol: IrSymbol): DeclarationIrBuilder =
    DeclarationIrBuilder(this, symbol, symbol.owner.startOffset, symbol.owner.endOffset)

class MyCodeIrGenerator(
    private val pluginContext: IrPluginContext,
    private val module: PluginContext,
) : IrVisitor<Unit, Nothing?>() {
    override fun visitElement(
        element: IrElement,
        data: Nothing?,
    ) {
        element.acceptChildren(this, data)
    }

    val toSymbol: IrSimpleFunctionSymbol = pluginContext.referenceFunctions(
        CallableId(FqName("kotlin"), null, Name.identifier("to"))
    ).singleOrNull() ?: error("Couldn't find to function")

    val mapOfSymbol: IrSimpleFunctionSymbol = pluginContext.referenceFunctions(
        CallableId(FqName("kotlin.collections"), null, Name.identifier("mapOf"))
    ).singleOrNull {
        it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().isVararg
    } ?: error("Couldn't find mapOf function")

    val mapEndpoint =
        pluginContext.referenceClass(module.classIds.mapEndpoint) ?: error("Couldn't find MapEndpoint class")
    val pairClass = pluginContext.referenceClass(ClassId.fromString("kotlin/Pair"))?.let {
        val stringType = pluginContext.symbols.string.defaultType
        val anyNullableType = pluginContext.symbols.any.typeWith().makeNullable()
        it.typeWith(listOf(stringType, anyNullableType))
    } ?: error("Couldn't find Pair class")

    override fun visitConstructor(declaration: IrConstructor, data: Nothing?) {
        val keyOrNull =
            (declaration.origin as? IrDeclarationOrigin.GeneratedByPlugin)?.pluginKey as? MyCodeGenerationExtension.Key
                ?: return
        val primaryConstructor = (declaration.parent as? IrClassImpl)?.primaryConstructor ?: return
        val irBuilder = pluginContext.createIrBuilder(declaration.symbol)

        val pairExpressions = declaration.valueParameters.map { param ->
            irBuilder.irCall(toSymbol).apply {
                insertExtensionReceiver(param.name.asString().toIrConst(pluginContext.symbols.string.defaultType))
                putValueArgument(0, irBuilder.irGet(declaration.valueParameters[param.index]))
                putTypeArgument(0, pluginContext.symbols.string.defaultType)
                putTypeArgument(1, pluginContext.symbols.any.defaultType.makeNullable())
            }
        }

        val mapOfCall = irBuilder.irCall(mapOfSymbol).apply {
            putValueArgument(0, irBuilder.irVararg(pairClass, pairExpressions))
            putTypeArgument(0, pluginContext.symbols.string.defaultType)
            putTypeArgument(1, pluginContext.symbols.any.defaultType.makeNullable())
        }

        val mapEndpointCall = irBuilder.irCall(mapEndpoint.constructors.single()).apply {
            putValueArgument(0, mapOfCall)
        }

        declaration.body = irBuilder.irBlockBody {
            +irDelegatingConstructorCall(primaryConstructor).apply {
                putValueArgument(0, mapEndpointCall)
            }
        }

        super.visitConstructor(declaration, data)
    }
}
