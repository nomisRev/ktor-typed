package io.github.nomisrev.typedapi.compiler.plugin.ir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import io.github.nomisrev.typedapi.compiler.plugin.fir.EndpointFactoryExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.ir.returnType
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.declarations.addConstructor
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addTypeParameter
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.builders.declarations.buildConstructor
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irDelegatingConstructorCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irRawFunctionReference
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irRichFunctionReference
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrInstanceInitializerCallImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.findDeclaration
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.irConstructorCall
import org.jetbrains.kotlin.ir.util.isAnnotationWithEqualFqName
import org.jetbrains.kotlin.ir.util.isBuiltInSuspendCoroutineUninterceptedOrReturn
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.toIrConst
import org.jetbrains.kotlin.ir.visitors.IrVisitor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.util.OperatorNameConventions

class EndpointFactoryIrExtension(
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
     * class Test(api: EndpointAPI) {
     *     companion object : EndpointFactory<Test> {
     *         override fun <B> create(block: (path: String, (EndpointAPI) -> Test) -> B): B =
     *             block("path-from-annotation", { api -> Test(api) })
     *     }
     * }
     */
    @UnsafeDuringIrConstructionAPI
    override fun visitClass(declaration: IrClass, data: Nothing?) {
        module.logger.log { "################################################################ Generating factory for ${declaration.name}" }
        val keyOrNull =
            (declaration.origin as? IrDeclarationOrigin.GeneratedByPlugin)?.pluginKey as? EndpointFactoryExtension.Key
                ?: return super.visitClass(declaration, data)

        val primaryConstructor = (declaration.parent as? IrClassImpl)?.primaryConstructor ?: return
        val irBuilder = pluginContext.createIrBuilder(declaration.symbol)

        val constructor = declaration.addConstructor {
            name = declaration.name
            isPrimary = true
            visibility = DescriptorVisibilities.PRIVATE
        }
        declaration.addDefaultConstructor(constructor)

        val type =
            pluginContext.referenceClass(module.classIds.factory)!!
                .typeWith(declaration.parentAsClass.defaultType)

        declaration.superTypes = listOf(type)

        /**
         *  fun <B> create(block: (path: String, (EndpointAPI) -> A) -> B): B =
         *      block(annotation.path, primaryConstructor)
         */
        val create = declaration.findDeclaration<IrSimpleFunction> { it.name == Name.identifier("create") } ?: return
        create.apply {
            val typeParam = typeParameters.single()
            val endpointAPIType = pluginContext.referenceClass(module.classIds.api)!!.defaultType
            // Create (EndpointAPI) -> Header type
            val functionType1 = pluginContext.irBuiltIns.functionN(1)
                .typeWith(listOf<IrType>(endpointAPIType, declaration.parentAsClass.defaultType))

            val invokeFun = irSymbols.function2?.owner?.declarations?.filterIsInstance<IrSimpleFunction>()
                ?.first { it.name == OperatorNameConventions.INVOKE }
                ?: error("No invoke function found")

            returnType = typeParam.defaultType
            val blockParameter = create.parameters[1]

            body = pluginContext.createIrBuilder(this.symbol).irBlockBody {
                // Get the annotation path
                val annotationPath = getAnnotationPath(declaration.parentAsClass)

                val invokeCall = irCall(invokeFun.symbol, type = typeParam.defaultType).apply {
                    // Receiver: set the `block` function parameter as receiver for the lambda invocation
                    dispatchReceiver = irGet(blockParameter)

                    arguments[1] = annotationPath.toIrConst(pluginContext.symbols.string.defaultType)

                    val lambdaFunction = pluginContext.irFactory.buildFun {
                        name = Name.special("<anonymous>")
                        returnType = declaration.parentAsClass.defaultType
                        visibility = DescriptorVisibilities.LOCAL
                        origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
                    }.apply {
                        parent = create
                        val apiParam = addValueParameter {
                            name = Name.identifier("api")
                            this.type = endpointAPIType
                        }
                        val builder = pluginContext.createIrBuilder(symbol)
                        body = builder.irBlockBody {
                            +irReturn(
                                irCall(primaryConstructor).apply {
                                    arguments[0] = irGet(apiParam)
                                }
                            )
                        }
                    }

                    arguments[2] = IrFunctionExpressionImpl(
                        startOffset = startOffset,
                        endOffset = endOffset,
                        type = functionType1,
                        function = lambdaFunction,
                        origin = IrStatementOrigin.LAMBDA
                    )
                }

                +irReturn(invokeCall)
            }
        }

        super.visitClass(declaration, data)
    }

    private fun getAnnotationPath(parentClass: IrClass): String {
        val annotation = parentClass.annotations.find { it.isAnnotationWithEqualFqName(module.classIds.annotation) }
            ?: error("Endpoint annotation not found on ${parentClass.name}")

        val pathArgument = annotation.arguments[0] as? IrConstImpl
            ?: error("Expected annotation argument to be a string literal")
        return pathArgument.value as? String
            ?: error("Expected annotation argument to be a string")
    }

    private fun IrClass.addDefaultConstructor(constructor: IrConstructor) {
        constructor.body = pluginContext.createIrBuilder(constructor.symbol).irBlockBody {
            +irDelegatingConstructorCall(context.irBuiltIns.anyClass.owner.constructors.single())
            +IrInstanceInitializerCallImpl(
                startOffset = startOffset,
                endOffset = endOffset,
                classSymbol = this@addDefaultConstructor.symbol,
                type = context.irBuiltIns.unitType,
            )
        }
    }

    private fun shouldGenerateCompanionObject(irClass: IrClass): Boolean {
        return irClass.hasAnnotation(module.classIds.annotation)
    }

    private fun generateCompanionObject(parentClass: IrClass) {
        val existingCompanion = parentClass.declarations.find {
            it is IrClass && it.isCompanion
        }
        if (existingCompanion != null) return

        val factory = pluginContext.irFactory

        val companionObject = factory.buildClass {
            name = SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT
            kind = org.jetbrains.kotlin.descriptors.ClassKind.OBJECT
            visibility = DescriptorVisibilities.PUBLIC
            modality = Modality.FINAL
            isCompanion = true
        }.apply {
            parent = parentClass

            val factoryType = createFactoryType(parentClass)
            superTypes = listOf(factoryType)

            val constructor = factory.buildConstructor {
                visibility = DescriptorVisibilities.PRIVATE
                returnType = defaultType
                parent = parentClass
            }

            declarations.add(constructor)
        }

        parentClass.declarations.add(companionObject)
    }

    private fun createFactoryType(forClass: IrClass): IrType {
        val factoryClassSymbol = pluginContext.referenceClass(module.classIds.factory)
            ?: error("Cannot find EndpointFactory class")

        return factoryClassSymbol.typeWith(listOf(forClass.defaultType))
    }
}