package io.github.nomisrev.typedapi.compiler.plugin.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import io.github.nomisrev.typedapi.compiler.plugin.fir.MyCodeGenerationExtension
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irDelegatingConstructorCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrPropertyImpl
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
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

    // TODO Support all inputs
    val Query = pluginContext.referenceFunctions(
        CallableId(FqName("io.github.nomisrev.typedapi"), null, Name.identifier("Query"))
    ).singleOrNull() ?: error("Couldn't find query function")

    val Path = pluginContext.referenceFunctions(
        CallableId(FqName("io.github.nomisrev.typedapi"), null, Name.identifier("Path"))
    ).singleOrNull() ?: error("Couldn't find path function")

    val Header = pluginContext.referenceFunctions(
        CallableId(FqName("io.github.nomisrev.typedapi"), null, Name.identifier("Header"))
    ).singleOrNull() ?: error("Couldn't find path function")

    val Body = pluginContext.referenceFunctions(
        CallableId(FqName("io.github.nomisrev.typedapi"), null, Name.identifier("Body"))
    ).singleOrNull() ?: error("Couldn't find path function")

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

    override fun visitFunction(declaration: IrFunction, data: Nothing?) {
        if (declaration.origin is IrDeclarationOrigin.GeneratedByPlugin) {
            module.logger.log { "Generating function: ${declaration.name}" }
            when(declaration.name.asString()) {
               "query" -> {
                   val builder = pluginContext.createIrBuilder(declaration.symbol)
                   declaration.body = body(builder, declaration, declaration.parentAsClass, "query", declaration.valueParameters.single())
               }
               "path" -> {
                   val builder = pluginContext.createIrBuilder(declaration.symbol)
                   declaration.body = body(builder, declaration, declaration.parentAsClass, "path", declaration.valueParameters.single())
               }
               "header" -> {
                   val builder = pluginContext.createIrBuilder(declaration.symbol)
                   declaration.body = body(builder, declaration, declaration.parentAsClass, "header", declaration.valueParameters.single())
               }
                "body" -> {
                   val builder = pluginContext.createIrBuilder(declaration.symbol)
                   declaration.body = body(builder, declaration, declaration.parentAsClass, "body", declaration.valueParameters.single())
                }
            }
        }
        super.visitFunction(declaration, data)
    }

    /**
     * TODO replace _prop with just inlining the Input.Query (etc)
     *   calls inside of the builder making everything lazy-and memoryless
     */
    private fun body(
        builder: DeclarationIrBuilder,
        function: IrFunction,
        endpoint: IrClass,
        type: String,
        api: IrValueParameter
    ): IrBlockBody = builder.irBlockBody {
        val block = function.valueParameters.singleOrNull()
        val inputType = when (type) {
            "query" -> Query
            "path" -> Path
            "header" -> Header
            "body" -> Body
            else -> error("Unknown type: $type")
        }

        val blockType = block?.type as? IrSimpleType ?: error("Lambda parameter is not a simple type")
        val invokeFun = blockType.classOrNull?.functions
            ?.single { it.owner.name.asString() == "invoke" }
            ?: error("Cannot find 'invoke' function on lambda type")

        val props = endpoint.declarations
            .filterIsInstance<IrPropertyImpl>()
            .filter {
                ((it.backingField?.initializer?.expression as IrCallImpl).dispatchReceiver as IrCallImpl).symbol.owner.name.asString() == type
            }
        val api = endpoint.primaryConstructor!!.valueParameters.single()
            // backinfField.initializer.expression.dispatchReceiver
        // For each delegated property, find its generated counterpart and call the block.
        for (defined in props) {
            // Generate the call to `block.invoke(this.age, Input.Query<String>())`.
            +irCall(invokeFun).apply {
                // Receiver: set the `block` function parameter as receiver for the lambda invocation
                dispatchReceiver = irGet(block)

                // Argument 1: Get the actual value `this.age`
                putValueArgument(0, irCall(defined.getter!!).apply {
                    this.dispatchReceiver = irGet(function.dispatchReceiverParameter!!)
                })

                // Argument 2: The value of the generated input property (e.g., `Query<String>()`).
                putValueArgument(1, irCall(inputType).apply {
                    putTypeArgument(0, defined.getter!!.returnType)
                    // TODO pass parameters
                    //   it.valueParameters.forEach { param ->
                    //       putValueArgument(param.index, irBuilder.irGet(param))
                    //   }
                })
            }
        }

        module.logger.log { "Generated query for: ${endpoint.name} with lambda: ${block.name}" }
    }
}
