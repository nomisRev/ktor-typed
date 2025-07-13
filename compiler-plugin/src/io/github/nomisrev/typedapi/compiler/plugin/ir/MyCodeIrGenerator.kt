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
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrPropertyImpl
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrExpressionBodyImpl
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

    override fun visitClass(declaration: IrClass, data: Nothing?) {
        val generated = declaration.declarations
            .filter { (it.origin as? IrDeclarationOrigin.GeneratedByPlugin)?.pluginKey is MyCodeGenerationExtension.Key }
        val members = generated.filterIsInstance<IrPropertyImpl>()
        val functions = generated.filterIsInstance<IrFunction>()

        val query = functions.firstOrNull { it.name.asString() == "query" }
        val path = functions.firstOrNull { it.name.asString() == "path" }
        val header = functions.firstOrNull { it.name.asString() == "header" }
        val body = functions.firstOrNull { it.name.asString() == "body" }

        val apiParameter = declaration.primaryConstructor?.valueParameters?.firstOrNull() ?: return

        if (members.isNotEmpty()) {
            module.logger.log { "Generating class: ${declaration.name}. $members" }
            members.forEach {
                val returnType = it.backingField?.type
                val irBuilder = pluginContext.createIrBuilder(it.symbol)

                val queryCall = irBuilder.irCall(Query).apply {
                    extensionReceiver = irBuilder.irGet(apiParameter)
                    putTypeArgument(0, returnType)
                    // TODO pass parameters
                    //                    it.valueParameters.forEach { param ->
                    //                        putValueArgument(param.index, irBuilder.irGet(param))
                    //                    }
                }
                it.backingField?.initializer = irBuilder.irExprBody(queryCall)
//                it.body = irBuilder.irBlockBody {
//                    +irReturn(queryCall)
//                }
            }
        }
        if (query != null && query.valueParameters.singleOrNull() != null) {
            val builder = pluginContext.createIrBuilder(query.symbol)
            // TODO Inside of the `query(block: (Any?, Input<Any?>) -> Unit)` body
            //   We need to call the block lambda for every member we generated in the previous step.
            //   So for `MyEndpoint.age` & its generated `MyEndpoint._api` we need to call `block(MyEndpoint.age, MyEndpoint._age)`.
            query.body = body(builder, query, declaration, "query")
        }
        if (path != null && path.valueParameters.singleOrNull() != null) {
            val builder = pluginContext.createIrBuilder(path.symbol)
            // TODO Inside of the `query(block: (Any?, Input<Any?>) -> Unit)` body
            //   We need to call the block lambda for every member we generated in the previous step.
            //   So for `MyEndpoint.age` & its generated `MyEndpoint._api` we need to call `block(MyEndpoint.age, MyEndpoint._age)`.
            path.body = body(builder, path, declaration, "path")
        }

        if (header != null && header.valueParameters.singleOrNull() != null) {
            val builder = pluginContext.createIrBuilder(header.symbol)
            // TODO Inside of the `query(block: (Any?, Input<Any?>) -> Unit)` body
            //   We need to call the block lambda for every member we generated in the previous step.
            //   So for `MyEndpoint.age` & its generated `MyEndpoint._api` we need to call `block(MyEndpoint.age, MyEndpoint._age)`.
            header.body = body(builder, header, declaration, "header")
        }
        if (body != null && body.valueParameters.singleOrNull() != null) {
            val builder = pluginContext.createIrBuilder(body.symbol)
            // TODO Inside of the `query(block: (Any?, Input<Any?>) -> Unit)` body
            //   We need to call the block lambda for every member we generated in the previous step.
            //   So for `MyEndpoint.age` & its generated `MyEndpoint._api` we need to call `block(MyEndpoint.age, MyEndpoint._age)`.
            body.body = body(builder, body, declaration, "body")
        }

        super.visitClass(declaration, data)
    }

    private fun body(
        builder: DeclarationIrBuilder,
        query: IrFunction,
        declaration: IrClass,
        type: String
    ): IrBlockBody = builder.irBlockBody {
        val block = query.valueParameters.singleOrNull()
        val dispatchReceiver = query.dispatchReceiverParameter!!
        val irClass = query.parentAsClass
        val generatedInputProperties = irClass.properties
            .filter { (it.origin as? IrDeclarationOrigin.GeneratedByPlugin)?.pluginKey == MyCodeGenerationExtension.Key }
            .associateBy { it.name.asString().removePrefix("_") }

        // ((definedProperties.single().backingField?.initializer?.expression as IrCallImpl).dispatchReceiver as? IrSimpleFunctionSymbolImpl)?.owner?.name

        val defined = irClass.properties.filter {
            (((irClass.properties.first().backingField?.initializer as? IrExpressionBodyImpl)?.expression as? IrCallImpl)?.dispatchReceiver as? IrCallImpl)?.symbol?.owner?.name?.identifier == type
        }

        val propToActual = defined.mapNotNull { prop ->
            generatedInputProperties[prop.name.asString()]?.let { prop to it }
        }

        val blockType = block?.type as? IrSimpleType
            ?: error("Lambda parameter is not a simple type")
        val invokeFun = blockType.classOrNull?.functions
            ?.single { it.owner.name.asString() == "invoke" }
            ?: error("Cannot find 'invoke' function on lambda type")

        // For each delegated property, find its generated counterpart and call the block.
        for ((defined, generated) in propToActual) {
            // Generate the call to `block.invoke(this.age, this._age)`.
            +irCall(invokeFun).apply {
                // The receiver for `invoke` is the lambda object itself.
                this.dispatchReceiver = irGet(block)

                // Argument 1: The value of the delegated property (e.g., `this.age`).
                val delegatedPropValue = irCall(defined.getter!!).apply {
                    // The receiver for the getter is the class instance (`this`).
                    this.dispatchReceiver = irGet(dispatchReceiver)
                }
                putValueArgument(0, delegatedPropValue)

                // Argument 2: The value of the generated input property (e.g., `this._age`).
                val generatedInputPropValue = irCall(generated.getter!!).apply {
                    this.dispatchReceiver = irGet(dispatchReceiver)
                }
                putValueArgument(1, generatedInputPropValue)
            }
        }


        module.logger.log { "Generated query for: ${declaration.name} with lambda: ${block.name}" }
    }

//    override fun visitFunction(declaration: IrFunction, data: Nothing?) {
//        // Check if the function is an IrSimpleFunction
//        val simpleFunction = declaration as? IrSimpleFunction ?: return super.visitFunction(declaration, data)
//
//        // Check if the function is in an endpoint class
//        val parentClass = simpleFunction.parent as? IrClass ?: return super.visitFunction(declaration, data)
//        val isEndpoint = parentClass.constructors.toList().any {
//            (it.origin as? IrDeclarationOrigin.GeneratedByPlugin)?.pluginKey as? MyCodeGenerationExtension.Key != null
//        }
//
//        if (isEndpoint) {
//            // Get the function name
//            val functionName = simpleFunction.name.asString()
//
//            // Skip standard methods like equals, hashCode, toString
//            if (functionName == "equals" || functionName == "hashCode" || functionName == "toString") {
//                return super.visitFunction(declaration, data)
//            }
//
//            // Check if it's a property getter (starts with "<get-")
//            if (functionName.startsWith("<get-")) {
//                // Extract the property name from the getter name (remove "<get-" and ">")
//                val propertyName = functionName.substring(5, functionName.length - 1)
//
//                // Skip standard Object properties and other special properties
//                if (propertyName == "class" || propertyName == "javaClass") {
//                    return super.visitFunction(declaration, data)
//                }
//
//                // Only generate code for properties that are delegated using functions from io.github.nomisrev.typedapi
//                // For now, we only know that the 'age' property is delegated using functions from io.github.nomisrev.typedapi
//                if (propertyName == "age") {
//                    // Get the first parameter which should be the api: EndpointAPI
//                    val apiParameter = parentClass.primaryConstructor?.valueParameters?.firstOrNull()
//                    if (apiParameter != null) {
//                        val irBuilder = pluginContext.createIrBuilder(simpleFunction.symbol)
//
//                        // Extract the type from the function's return type
//                        val returnType = simpleFunction.returnType
//
//                        // Create a call to api.query<Type>()
//                        val queryCall = irBuilder.irCall(Query).apply {
//                            extensionReceiver = irBuilder.irGet(apiParameter)
//                            putTypeArgument(0, returnType)
//
//                        }
//
//                        // Return the result of the query call
//                        simpleFunction.body = irBuilder.irBlockBody {
//                            +irReturn(queryCall)
//                        }
//
//                        module.logger.log { "Generated property delegation with query for: ${functionName}" }
//                    }
//                }
//            }
//        }
//
//        super.visitFunction(declaration, data)
//    }
}
