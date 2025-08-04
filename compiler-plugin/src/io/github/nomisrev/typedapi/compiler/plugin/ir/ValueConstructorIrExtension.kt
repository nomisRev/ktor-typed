package io.github.nomisrev.typedapi.compiler.plugin.ir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import io.github.nomisrev.typedapi.compiler.plugin.fir.HttpRequestValueExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irDelegatingConstructorCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.toIrConst
import org.jetbrains.kotlin.ir.visitors.IrVisitor

class ValueConstructorIrExtension(
    private val pluginContext: IrPluginContext,
    private val module: PluginContext,
    private val irSymbols: IrSymbols,
) : IrVisitor<Unit, Nothing?>() {
    override fun visitElement(
        element: IrElement,
        data: Nothing?,
    ) {
        element.acceptChildren(this, data)
    }

    /**
     * Generates a value constructor for the Endpoint.
     * This constructor takes a Map<String, Any?> as input,
     * and maps the parameters to the constructor arguments.
     *
     * constructor(map: Map<String, Any?>) : this(
     *      mapOf(
     *          "path" to path,
     *          "method" to method,
     *          "body" to body,
     *          "headers" to headers,
     *          "query" to query,
     *      )
     *  )
     */
    override fun visitConstructor(declaration: IrConstructor, data: Nothing?) {
        val keyOrNull =
            (declaration.origin as? IrDeclarationOrigin.GeneratedByPlugin)?.pluginKey as? HttpRequestValueExtension.Key
                ?: return
        val primaryConstructor = (declaration.parent as? IrClassImpl)?.primaryConstructor ?: return
        val irBuilder = pluginContext.createIrBuilder(declaration.symbol)

        val pairExpressions = declaration.parameters.map { param ->
            irBuilder.irCall(irSymbols.to).apply {
                insertExtensionReceiver(param.name.asString().toIrConst(pluginContext.symbols.string.defaultType))
                arguments[1] = irBuilder.irGet(declaration.parameters[param.indexInParameters])
                typeArguments[0] = pluginContext.symbols.string.defaultType
                typeArguments[1] = pluginContext.symbols.any.defaultType.makeNullable()
            }
        }

        val mapOfCall = irBuilder.irCall(irSymbols.arrayOf).apply {
            arguments[0] = irBuilder.irVararg(irSymbols.stringToNullableAny, pairExpressions)
            typeArguments[0] = irSymbols.stringToNullableAny
        }

        val mapEndpointCall =
            irBuilder.irCall(irSymbols.mapEndpoint.constructors.single { !it.owner.isPrimary }).apply {
                arguments[0] = mapOfCall
            }

        declaration.body = irBuilder.irBlockBody {
            +irDelegatingConstructorCall(primaryConstructor).apply {
                arguments[0] = mapEndpointCall
            }
        }

        super.visitConstructor(declaration, data)
    }

}