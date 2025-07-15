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
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrPropertyImpl
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrStringConcatenationImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.isString
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.isAnnotationWithEqualFqName
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.toIrConst
import org.jetbrains.kotlin.ir.visitors.IrVisitor
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.resolve.calls.inference.constraintPosition.valueParameterPosition
import org.jetbrains.kotlin.util.OperatorNameConventions

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

    val arrayOfSymbol: IrSimpleFunctionSymbol = pluginContext.referenceFunctions(
        CallableId(FqName("kotlin"), null, Name.identifier("arrayOf"))
    ).single {
        // Looking for fun <T> arrayOf(vararg elements: T): Array<T>
        it.owner.typeParameters.size == 1 && it.owner.parameters.size == 1 && it.owner.parameters[0].isVararg
    }

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

    val function2 = pluginContext.referenceClass(StandardClassIds.FunctionN(2))

    override fun visitConstructor(declaration: IrConstructor, data: Nothing?) {
        val keyOrNull =
            (declaration.origin as? IrDeclarationOrigin.GeneratedByPlugin)?.pluginKey as? MyCodeGenerationExtension.Key
                ?: return
        val primaryConstructor = (declaration.parent as? IrClassImpl)?.primaryConstructor ?: return
        val irBuilder = pluginContext.createIrBuilder(declaration.symbol)

        val pairExpressions = declaration.parameters.map { param ->
            irBuilder.irCall(toSymbol).apply {
                insertExtensionReceiver(param.name.asString().toIrConst(pluginContext.symbols.string.defaultType))
                arguments[1] = irBuilder.irGet(declaration.parameters[param.indexInParameters])
                typeArguments[0] = pluginContext.symbols.string.defaultType
                typeArguments[1] = pluginContext.symbols.any.defaultType.makeNullable()
            }
        }

        val mapOfCall = irBuilder.irCall(arrayOfSymbol).apply {
            arguments[0] = irBuilder.irVararg(pairClass, pairExpressions)
            typeArguments[0] = pairClass
        }

        val mapEndpointCall = irBuilder.irCall(mapEndpoint.constructors.single { !it.owner.isPrimary }).apply {
            arguments[0] = mapOfCall
        }

        declaration.body = irBuilder.irBlockBody {
            +irDelegatingConstructorCall(primaryConstructor).apply {
                arguments[0] = mapEndpointCall
            }
        }

        super.visitConstructor(declaration, data)
    }

    override fun visitFunction(declaration: IrFunction, data: Nothing?) {
        if (declaration.origin is IrDeclarationOrigin.GeneratedByPlugin) {
            module.logger.log { "Generating function: ${declaration.name}" }
            when (declaration.name.asString()) {
                "query" -> {
                    val builder = pluginContext.createIrBuilder(declaration.symbol)
                    declaration.body = body(
                        builder,
                        declaration,
                        declaration.parentAsClass,
                        "query"
                    )
                }

                "path" -> {
                    val builder = pluginContext.createIrBuilder(declaration.symbol)
                    if (declaration.parameters.size == 1) {
                        declaration.body = buildPathString(builder, declaration)
                    } else declaration.body = body(
                        builder,
                        declaration,
                        declaration.parentAsClass,
                        "path"
                    )
                }

                "header" -> {
                    val builder = pluginContext.createIrBuilder(declaration.symbol)
                    declaration.body = body(
                        builder,
                        declaration,
                        declaration.parentAsClass,
                        "header"
                    )
                }

                "body" -> {
                    val builder = pluginContext.createIrBuilder(declaration.symbol)
                    declaration.body = body(
                        builder,
                        declaration,
                        declaration.parentAsClass,
                        "body"
                    )
                }
            }
        }
        super.visitFunction(declaration, data)
    }

    /**
     * Builds String template from @Endpoint(path) where path needs to replace {{propName}} with propName values.
     * So for "/user/{userId}/other/{value}" it needs to replace {userId} and {value} with ${this.userId} and ${this.value}.
     */
    fun buildPathString(builder: DeclarationIrBuilder, declaration: IrFunction): IrExpressionBody {
        val parentClass = declaration.parentAsClass
        val annotation = parentClass.annotations.find { it.isAnnotationWithEqualFqName(module.classIds.annotation) }
            ?: error("Endpoint annotation not found on ${parentClass.name}")

        val pathArgument = annotation.arguments[0] as? IrConstImpl
            ?: error("Expected annotation argument to be a string literal")
        val pathString = pathArgument.value as? String
            ?: error("Expected annotation argument to be a string")

        val propertiesByName = parentClass.declarations
            .filterIsInstance<IrProperty>()
            .associateBy { it.name.asString() }

        val irExpressions = mutableListOf<IrExpression>()
        val regex = """\{([^}]+)\}""".toRegex()
        var lastIndex = 0

        regex.findAll(pathString).forEach { matchResult ->
            // Add the static part of the string before the placeholder.
            if (matchResult.range.first > lastIndex) {
                val staticPart = pathString.substring(lastIndex, matchResult.range.first)
                irExpressions.add(builder.irString(staticPart))
            }

            // Handle the placeholder.
            val propertyName = matchResult.groupValues[1]
            val property = propertiesByName[propertyName]
                ?: error("Cannot find property '$propertyName' in class '${parentClass.name.asString()}' for path template.")

            // Create an expression to get the property value, e.g., `this.userId`.
            val propertyGetterCall = builder.irCall(property.getter!!).apply {
                dispatchReceiver = builder.irGet(declaration.dispatchReceiverParameter!!)
            }
            irExpressions.add(propertyGetterCall)

            lastIndex = matchResult.range.last + 1
        }

        if (lastIndex < pathString.length) {
            val staticPart = pathString.substring(lastIndex)
            irExpressions.add(builder.irString(staticPart))
        }

        val finalExpression = when {
            // Path was empty or contained no placeholders
            irExpressions.isEmpty() -> builder.irString(pathString)
            // Path is a single element, e.g. "{userId}" or "just-a-string"
            irExpressions.size == 1 -> {
                val expr = irExpressions.single()
                if (expr !is IrConstImpl && !expr.type.isString()) {
                    val toStringSymbol = pluginContext.irBuiltIns.anyClass.functions.single {
                        it.owner.name.asString() == "toString" && it.owner.parameters.isEmpty()
                    }
                    builder.irCall(toStringSymbol).apply {
                        dispatchReceiver = expr
                    }
                } else {
                    expr
                }
            }
            // Path is a combination of strings and properties, build a string concatenation
            else -> IrStringConcatenationImpl(
                builder.startOffset,
                builder.endOffset,
                pluginContext.symbols.string.defaultType,
                irExpressions
            )
        }


        return builder.irExprBody(finalExpression)
    }

    /**
     * TODO replace _prop with just inlining the Input.Query (etc)
     *   calls inside of the builder making everything lazy-and memoryless
     */
    private fun body(
        builder: DeclarationIrBuilder,
        function: IrFunction,
        endpoint: IrClass,
        type: String
    ): IrBlockBody = builder.irBlockBody {
        val block = function.parameters.firstOrNull { it.name.asString() == "block" } ?: return@irBlockBody
        val inputType = when (type) {
            "query" -> Query
            "path" -> Path
            "header" -> Header
            "body" -> Body
            else -> error("Unknown type: $type")
        }

        module.logger.log {
            """
                Searching for invoke:
                ${block?.dump()}
            """.trimIndent()
        }


        val invokeFun = function2?.owner?.declarations?.filterIsInstance<IrSimpleFunction>()
            ?.first { it.name == OperatorNameConventions.INVOKE }
            ?: error("No invoke function found")


//        val invokeFun = block?.type?.classOrNull?.functions?.first {
//            it.owner.name == OperatorNameConventions.INVOKE
//        } ?: error("No invoke function found")

        val inputs = endpoint.declarations
            .filterIsInstance<IrPropertyImpl>()
            .filter {
                ((it.backingField?.initializer?.expression as IrCallImpl).dispatchReceiver as IrCallImpl).symbol.owner.name.asString() == type
            }

        for (input in inputs) {
            // Generate the call to `block.invoke(this.age, Input.Query<String>())`.
            +irCall(invokeFun).apply {
                // Receiver: set the `block` function parameter as receiver for the lambda invocation
                dispatchReceiver = irGet(block ?: error("No block parameter found"))

                // Argument 1: Get the actual value `this.age`
                arguments[1] = irCall(input.getter!!).apply {
                    this.dispatchReceiver = irGet(function.dispatchReceiverParameter!!)
                }

                // Argument 2: The value of the generated input property (e.g., `Query<String>(name)`).
                arguments[2] = irCall(inputType).apply {
                    typeArguments[0] = input.getter!!.returnType
                    // TODO: consider the specified name!
                    if (type != "body") {
                        arguments[0] = irString(input.name.asString())
                    }
                    // TODO pass parameters
                    //   it.valueParameters.forEach { param ->
                    //       putValueArgument(param.index, irBuilder.irGet(param))
                    //   }
                }
            }
        }

        module.logger.log { "Generated query for: ${endpoint.name} with lambda: ${block?.name}" }
    }
}
