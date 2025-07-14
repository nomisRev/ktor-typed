package io.github.nomisrev.typedapi.compiler.plugin.fir

import com.intellij.openapi.progress.Task
import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.analysis.decompiler.stub.flags.VISIBILITY
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.containingClassForStaticMemberAttr
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.impl.FirDeclarationStatusImpl
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.plugin.createMemberProperty
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirTypeParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.ir.backend.js.utils.TODO
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtPsiFactory

class MyCodeGenerationExtension(
    session: FirSession,
    private val module: PluginContext,
) : FirDeclarationGenerationExtension(session) {
    private val predicate = LookupPredicate.create { annotated(module.classIds.annotation) }
    private val predicateBasedProvider = session.predicateBasedProvider
    private val matchedClasses by lazy {
        predicateBasedProvider.getSymbolsByPredicate(predicate).filterIsInstance<FirRegularClassSymbol>()
    }
    private val conversionFns = listOf(
        Name.identifier("query"),
        Name.identifier("path"),
        Name.identifier("header"),
        Name.identifier("body"),
    )

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(predicate)
    }

    object Key : GeneratedDeclarationKey()

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val endpoint = context.declaredScope?.classId?.let { classId -> matchedClasses.find { it.classId == classId } }
            ?: return emptyList()
        if (endpoint.rawStatus.isData) return emptyList()
        val properties = endpoint.declarationSymbols.filterIsInstance<FirPropertySymbol>()
        val constructor = createConstructor(endpoint, Key) {
            properties.forEach { prop ->
                valueParameter(prop.name, prop.resolvedReturnType)
            }
        }.apply {
            containingClassForStaticMemberAttr = endpoint.toLookupTag()
        }
        module.logger.log { "Generating constructor for $endpoint: (${constructor.valueParameters.joinToString { it.name.asString() }})" }
        return listOf(constructor.symbol)
    }

    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        val endpoint = context?.declaredScope?.classId?.let { classId -> matchedClasses.find { it.classId == classId } }
            ?: return emptyList()
        val name = callableId.callableName.asString()
        val inputType = when {
            name == "query" -> module.classIds.inputQuery
            name == "path" -> module.classIds.inputPath
            name == "header" -> module.classIds.inputHeader
            name == "body" -> module.classIds.inputBody
            else -> return emptyList()
        }

        return if (conversionFns.contains(callableId.callableName)) {
            // query { value: Any?, input: Input<Any?> -> ... }
            val lambdaType = StandardClassIds.FunctionN(2).constructClassLikeType(
                arrayOf(
                    session.builtinTypes.nullableAnyType.coneType,
                    inputType.constructClassLikeType(typeArguments = arrayOf(session.builtinTypes.nullableAnyType.coneType)),
                    session.builtinTypes.unitType.coneType
                )
            )

            val member = createMemberFunction(
                endpoint,
                Key,
                callableId.callableName,
                session.builtinTypes.unitType.coneType
            ) {
                valueParameter(Name.identifier("block"), lambdaType)
                status {
                    isOverride = true
                }
            }.apply {
                containingClassForStaticMemberAttr = endpoint.toLookupTag()
            }

            module.logger.log { "Generating 'Inspectable' for $endpoint.${member.name}" }

            listOfNotNull(member.symbol, pathStringOrNull(callableId, endpoint)?.symbol)
        } else {
            super.generateFunctions(callableId, context)
        }
    }

    private fun pathStringOrNull(callableId: CallableId, endpoint: FirRegularClassSymbol) =
        if (callableId.callableName.asString() == "path") {
            createMemberFunction(
                endpoint,
                Key,
                callableId.callableName,
                session.builtinTypes.stringType.coneType
            ) {
                status { isOverride = true }
            }.apply {
                containingClassForStaticMemberAttr = endpoint.toLookupTag()
            }
        } else null

//    override fun generateProperties(
//        callableId: CallableId,
//        context: MemberGenerationContext?
//    ): List<FirPropertySymbol> {
//        val endpoint = context
//            ?.declaredScope
//            ?.classId
//            ?.let { classId -> matchedClasses.find { it.classId == classId } }
//            ?: return emptyList()
//
//        if (conversionFns.contains(callableId.callableName)) return emptyList()
//
//        val prop = endpoint.declarationSymbols
//            .filterIsInstance<FirPropertySymbol>()
//            .find { it.name.asString() == callableId.callableName.asString().drop(1) }
//            ?: return emptyList()
//
//        return listOf(
//            createMemberProperty(
//                endpoint,
//                Key,
//                callableId.callableName,
//                module.classIds.input.constructClassLikeType(arrayOf(prop.resolvedReturnType))
//            ).apply {
//                containingClassForStaticMemberAttr = endpoint.toLookupTag()
//                module.logger.log { "generateProperties for $endpoint.$name" }
//            }.symbol
//        )
//    }

    /**
     * Generates:
     *  - constructor(inputs...) : this(MapEnpointAPI(name1 to input1, name2 to input2, etc)
     *  - For every input by input(..) it generates _input = Input()
     *      => TODO: In IR we should rewrite the body to use input(_input) for the original param.
     *  - query { value: Any?, input: Input<Any?> - > }
     */
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        return if (matchedClasses.contains(classSymbol)) {
            val callables = setOf(SpecialNames.INIT) /*+ classSymbol
                .declarationSymbols
                .filterIsInstance<FirPropertySymbol>()
                .filter { it.hasDelegate }
                .map { underscored(it) }*/ + conversionFns
            module.logger.log { "getCallableNamesForClass for ${classSymbol.classId}: $callables" }
            callables
        } else super.getCallableNamesForClass(classSymbol, context)
    }

    private fun underscored(symbol: FirPropertySymbol): Name =
        Name.identifier("_${symbol.name.asString()}")

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelClassIds(): Set<ClassId> {
        val topLevelIds = matchedClasses.mapTo(mutableSetOf()) {
            it.classId
        }
        module.logger.log { "Generating code for: ${topLevelIds.joinToString { it.shortClassName.asString() }}" }
        return topLevelIds
    }
}
