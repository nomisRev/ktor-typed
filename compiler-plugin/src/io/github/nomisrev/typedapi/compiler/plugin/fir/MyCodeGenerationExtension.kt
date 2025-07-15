package io.github.nomisrev.typedapi.compiler.plugin.fir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.containingClassForStaticMemberAttr
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.builder.buildPropertyAccessor
import org.jetbrains.kotlin.fir.expressions.FirFunctionCallOrigin
import org.jetbrains.kotlin.fir.expressions.builder.buildDelegatedConstructorCall
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildPropertyAccessExpression
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.renderWithType
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.ConeKotlinTypeConflictingProjection
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.types.ConstantValueKind
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly

val httpRequestValueIdentifiers = listOf(
    Name.identifier("query"),
    Name.identifier("path"),
    Name.identifier("header"),
    Name.identifier("body"),
)

// TODO remove SymbolInternals
// TODO what is DirectDeclarationsAccess, and can we avoid it?
class MyCodeGenerationExtension(
    session: FirSession,
    private val module: PluginContext,
) : FirDeclarationGenerationExtension(session) {
    private val predicate = LookupPredicate.create { annotated(module.classIds.annotation) }
    private val matchedClasses by lazy {
        session.predicateBasedProvider.getSymbolsByPredicate(predicate).filterIsInstance<FirRegularClassSymbol>()
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(predicate)
    }

    object Key : GeneratedDeclarationKey()

    private val toSymbol by lazy {
        val toCallableId = CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("to"))
        session.symbolProvider.getTopLevelFunctionSymbols(toCallableId.packageName, toCallableId.callableName)
            .singleOrNull() ?: error("Could not find kotlin.to symbol")
    }

    val pairSymbol by lazy {
        val pairClassId = ClassId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("Pair"))
        session.symbolProvider.getClassLikeSymbolByClassId(pairClassId) as? FirRegularClassSymbol
            ?: error("Cannot find kotlin.Pair symbol")
    }

    fun pairSymbol(param: FirValueParameter) = pairSymbol.constructType(
        typeArguments = arrayOf(
            ConeKotlinTypeConflictingProjection(session.builtinTypes.stringType.coneType),
            ConeKotlinTypeConflictingProjection(param.returnTypeRef.coneType)
        )
    )

    @OptIn(DirectDeclarationsAccess::class)
    private val mapEndpoint: FirRegularClassSymbol by lazy {
        session.symbolProvider.getClassLikeSymbolByClassId(module.classIds.mapEndpoint) as? FirRegularClassSymbol
            ?: error("Cannot find MapEndpointAPI class symbol")
    }

    @OptIn(DirectDeclarationsAccess::class)
    private val mapEndpointVarargConstructor by lazy {
        mapEndpoint.declarationSymbols.filterIsInstance<FirConstructorSymbol>()
            .first { it.valueParameterSymbols.singleOrNull()?.isVararg == true }
    }

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        if (context.owner.classId !in matchedClasses.map { it.classId }) return emptyList()

        val constructors = mutableListOf<FirConstructorSymbol>()
        val properties = mutableListOf<FirPropertySymbol>()
        @OptIn(DirectDeclarationsAccess::class) context.owner.declarationSymbols.forEach { symbol ->
            when (symbol) {
                is FirConstructorSymbol -> constructors.add(symbol)
                is FirPropertySymbol -> properties.add(symbol)
                else -> Unit
            }
        }
        val primaryConstructorSymbol = constructors.singleOrNull() ?: return emptyList()
        // TODO Generate default constructor
        if (properties.isEmpty()) return emptyList()

        module.logger.log { "generateConstructors. primaryConstructorSymbol: ${primaryConstructorSymbol.name.asString()}" }
        module.logger.log { "generateConstructors.  properties: ${properties.joinToString { it.name.asString() }}" }

        val constructor = createConstructor(context.owner, Key, isPrimary = false) {
            properties.map { valueParameter(it.name, it.resolvedReturnType) }
        }

        constructor.replaceDelegatedConstructor(buildDelegatedConstructorCall {
            isThis = true
            constructedTypeRef = context.owner.defaultType().toFirResolvedTypeRef()
            calleeReference = primaryConstructorSymbol.toResolvedNamedReference()

            val mapEndpointConstructorCall = buildFunctionCall {
                calleeReference = mapEndpointVarargConstructor.toResolvedNamedReference()
                coneTypeOrNull = mapEndpoint.defaultType()
                argumentList = constructor.valueParameters.associate { param ->
                    val nameToParamCall = buildFunctionCall {
                        extensionReceiver = buildLiteralExpression(
                            null, ConstantValueKind.String, param.name.asString(), setType = true
                        )
                        calleeReference = toSymbol.toResolvedNamedReference()
                        origin = FirFunctionCallOrigin.Infix
                        coneTypeOrNull = pairSymbol(param)
                        argumentList = mapOf(buildPropertyAccessExpression {
                            calleeReference = param.toResolvedNamedReference()
                            coneTypeOrNull = param.returnTypeRef.coneType
                        } to param).toResolvedArgumentList()
                    }
                    val targetParam =
                        @OptIn(SymbolInternals::class) mapEndpointVarargConstructor.valueParameterSymbols.single().fir
                    Pair(nameToParamCall, targetParam)
                }.toResolvedArgumentList()
            }

            @OptIn(SymbolInternals::class) val primaryConstructorValues =
                primaryConstructorSymbol.valueParameterSymbols.singleOrNull()?.fir
                    ?: error("Primary Constructor requires single parameter but found: ${constructor.valueParameters.joinToString { it.name.asString() }}")
            argumentList = mapOf(mapEndpointConstructorCall to primaryConstructorValues).toResolvedArgumentList()
        })

        module.logger.log { constructor.renderWithType() }

        return listOf(constructor.symbol)
    }

    override fun generateFunctions(
        callableId: CallableId, context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        val endpoint = context?.declaredScope?.classId?.let { classId -> matchedClasses.find { it.classId == classId } }
            ?: return emptyList()
        val name = callableId.callableName.asString().capitalizeAsciiOnly()
        val inputType = module.classIds.input.createNestedClassId(Name.identifier(name)) ?: return emptyList()

        return if (httpRequestValueIdentifiers.contains(callableId.callableName)) {
            // query { value: Any?, input: Input<Any?> -> ... }
            val lambdaType = StandardClassIds.FunctionN(2).constructClassLikeType(
                arrayOf(
                    session.builtinTypes.nullableAnyType.coneType,
                    inputType.constructClassLikeType(typeArguments = arrayOf(session.builtinTypes.nullableAnyType.coneType)),
                    session.builtinTypes.unitType.coneType
                )
            )
            val member = createMemberFunction(
                context.owner, Key, callableId.callableName, session.builtinTypes.unitType.coneType
            ) {
                valueParameter(Name.identifier("block"), lambdaType)
                status {
                    isOverride = true
                }
            }.apply {
                containingClassForStaticMemberAttr = endpoint.toLookupTag()
            }

            module.logger.log { "Generating 'Inspectable' for $endpoint.${member.name}" }
            module.logger.log { member.renderWithType() }

            listOfNotNull(member.symbol, pathStringOrNull(callableId, endpoint)?.symbol)
        } else {
            super.generateFunctions(callableId, context)
        }
    }

    private fun pathStringOrNull(callableId: CallableId, endpoint: FirRegularClassSymbol) =
        if (callableId.callableName.asString() == "path") {
            createMemberFunction(
                endpoint, Key, callableId.callableName, session.builtinTypes.stringType.coneType
            ) {
                status { isOverride = true }
            }.apply {
                containingClassForStaticMemberAttr = endpoint.toLookupTag()
            }
        } else null

    /**
     * We generate a special constructor for HttpRequestValue, and its interface functions
     */
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        return if (matchedClasses.contains(classSymbol)) {
            val callables = setOf(SpecialNames.INIT) + httpRequestValueIdentifiers
            module.logger.log { "getCallableNamesForClass for ${classSymbol.classId}: $callables" }
            callables
        } else super.getCallableNamesForClass(classSymbol, context)
    }

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelClassIds(): Set<ClassId> {
        val topLevelIds = matchedClasses.mapTo(mutableSetOf()) {
            it.classId
        }
        module.logger.log { "Generating code for: ${topLevelIds.joinToString { it.shortClassName.asString() }}" }
        return topLevelIds
    }
}
