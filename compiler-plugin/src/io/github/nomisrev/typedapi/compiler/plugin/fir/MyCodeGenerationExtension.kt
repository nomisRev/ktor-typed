package io.github.nomisrev.typedapi.compiler.plugin.fir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.containingClassForStaticMemberAttr
import org.jetbrains.kotlin.fir.containingClassLookupTag
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.FirVariable
import org.jetbrains.kotlin.fir.declarations.declaredProperties
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCallOrigin
import org.jetbrains.kotlin.fir.expressions.buildResolvedArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.buildDelegatedConstructorCall
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.impl.FirResolvedArgumentList
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.renderWithType
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.toSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
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

    // TODO
//    @OptIn(DirectDeclarationsAccess::class)
//    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
//        val endpoint = context.declaredScope?.classId?.let { classId -> matchedClasses.find { it.classId == classId } }
//            ?: return emptyList()
//        if (endpoint.rawStatus.isData) return emptyList()
//        val primaryConstructorSymbol = endpoint.declarationSymbols
//            .filterIsInstance<FirConstructorSymbol>()
//            .first { it.isPrimary }
//
//        val properties = endpoint.declarationSymbols.filterIsInstance<FirPropertySymbol>()
//        val constructor = createConstructor(context.owner, Key, isPrimary = false) {
//            properties.forEach { prop ->
//                valueParameter(prop.name, prop.resolvedReturnType)
//            }
//        }.apply {
//            replaceDelegatedConstructor(buildDelegatedConstructorCall {
//                isThis = true
//                constructedTypeRef = endpoint.defaultType().toFirResolvedTypeRef()
//                calleeReference = buildResolvedNamedReference {
//                    name = primaryConstructorSymbol.name
//                    resolvedSymbol = primaryConstructorSymbol
//                }
//
//                // Build the argument list
//                val mapping = LinkedHashMap<FirExpression, FirValueParameter>()
//                properties.forEachIndexed { index, prop ->
//                    primaryConstructorSymbol.valueParameterSymbols
//                    val parameterSymbol = primaryConstructorSymbol.fir.valueParameters[index]
//                    mapping[buildPropertyAccessExpression {
//                        calleeReference = buildResolvedNamedReference {
//                            name = prop.name
//                            resolvedSymbol = prop
//                        }
//                    }] = parameterSymbol
//                }
//
//                buildResolvedArgumentList(
//                    original = null, // If there's no original argument list
//                    mapping = mapping
//                ).also { resolvedList ->
//                    argumentList = resolvedList // Assign it to the constructor call
//                }
//
//                argumentList = buildArgumentList {
//                    // For each property, create a `"name" to value` expression
//                    properties.forEach { param ->
//                        // Each argument is an infix call to `to`, e.g., "propName" to propName
//                        arguments += buildFunctionCall {
//                            // The string literal "propName" is the receiver of the `to` call
//                            explicitReceiver = buildLiteralExpression(
//                                source = context.owner.source,
//                                kind = ConstantValueKind.String,
//                                value = param.name.asString(),
//                                setType = true,
//                            )
//                            calleeReference =
//                                buildSimpleNamedReference { name = Name.identifier("to") }
//
//                            // The argument for `to` is the value parameter from our new constructor
//                            argumentList = buildArgumentList {
//                                arguments += buildPropertyAccessExpression {
//                                    calleeReference = buildResolvedNamedReference {
//                                        name = param.name
//                                        resolvedSymbol = param
//                                    }
//                                }
//                            }
//
//                            // Important: Specify that this is an infix function call
//                            origin = FirFunctionCallOrigin.Infix
//                        }
//                    }
//                }
//            })
//            containingClassForStaticMemberAttr = endpoint.toLookupTag()
//        }
//        module.logger.log { "Generating constructor for $endpoint: (${constructor.valueParameters.joinToString { it.name.asString() }})" }
//        return listOf(constructor.symbol)
//    }

    private val toSymbol by lazy {
        val toCallableId = CallableId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("to"))
        session.symbolProvider.getTopLevelFunctionSymbols(toCallableId.packageName, toCallableId.callableName)
            .singleOrNull() // Use singleOrNull to be safe
            ?: error("Could not find kotlin.to symbol")
    }

    val pairSymbol by lazy {
        val pairClassId = ClassId(StandardClassIds.BASE_KOTLIN_PACKAGE, Name.identifier("Pair"))
        session.symbolProvider.getClassLikeSymbolByClassId(pairClassId) as? FirRegularClassSymbol
            ?: error("Cannot find kotlin.Pair symbol")
    }

    fun pairSymbol(param: FirValueParameter) =
        pairSymbol.constructType(
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
    private val mapEndpointApiVarargParam by lazy {
        mapEndpoint.declarationSymbols.filterIsInstance<FirConstructorSymbol>()
            .first { it.valueParameterSymbols.singleOrNull()?.isVararg == true }
    }

    // TODO remove SymbolInternals
    // TODO what is DirectDeclarationsAccess, and can we avoid it?
    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        if (context.owner.classId !in matchedClasses.map { it.classId }) return emptyList()

        val constructors = mutableListOf<FirConstructorSymbol>()
        val properties = mutableListOf<FirPropertySymbol>()
        @OptIn(DirectDeclarationsAccess::class)
        context.owner.declarationSymbols.forEach { symbol ->
            when (symbol) {
                is FirConstructorSymbol -> constructors.add(symbol)
                is FirPropertySymbol -> properties.add(symbol)
                else -> Unit
            }
        }
        val primaryConstructorSymbol = constructors.singleOrNull() ?: return emptyList()
        if (properties.isEmpty()) // TODO Generate default constructor

        module.logger.log { "generateConstructors. primaryConstructorSymbol: ${primaryConstructorSymbol.name.asString()}" }
        module.logger.log { "generateConstructors.  properties: ${properties.joinToString { it.name.asString() }}" }

        val constructor = createConstructor(context.owner, Key, isPrimary = false) {
            properties.map { valueParameter(it.name, it.resolvedReturnType) }
        }

        constructor.replaceDelegatedConstructor(buildDelegatedConstructorCall {
            isThis = true
            constructedTypeRef = context.owner.defaultType().toFirResolvedTypeRef()
            calleeReference = primaryConstructorSymbol.toResolvedNamedReference()

            val mapEndpointExpression = buildFunctionCall {
                calleeReference = mapEndpointApiVarargParam.toResolvedNamedReference()
                coneTypeOrNull = (mapEndpointApiVarargParam.containingClassLookupTag()
                    ?.toSymbol(session) as? FirRegularClassSymbol)?.defaultType()
                argumentList = constructor.valueParameters.associate { param ->
                    Pair(buildFunctionCall {
                        extensionReceiver = buildLiteralExpression(
                            null,
                            ConstantValueKind.String,
                            param.name.asString(),
                            setType = true
                        )
                        calleeReference = toSymbol.toResolvedNamedReference()
                        origin = FirFunctionCallOrigin.Infix
                        coneTypeOrNull = pairSymbol(param)
                        argumentList = mapOf(buildPropertyAccessExpression {
                            calleeReference = param.toResolvedNamedReference()
                            coneTypeOrNull = param.returnTypeRef.coneType
                        } to param).toResolvedArgumentList()
                    }, @OptIn(SymbolInternals::class) mapEndpointApiVarargParam.valueParameterSymbols.single().fir)
                }.toResolvedArgumentList()
            }

            @OptIn(SymbolInternals::class)
            val primaryConstructorValues = primaryConstructorSymbol.valueParameterSymbols.singleOrNull()?.fir
                ?: error("Primary Constructor requires single parameter but found: ${constructor.valueParameters.joinToString { it.name.asString() }}")
            argumentList = mapOf(mapEndpointExpression to primaryConstructorValues).toResolvedArgumentList()
        })

        module.logger.log { constructor.renderWithType() }

        return listOf(constructor.symbol)
    }

    fun Map<out FirExpression, FirValueParameter>.toResolvedArgumentList(): FirResolvedArgumentList {
        val original = buildArgumentList { arguments += keys }
        return buildResolvedArgumentList(original, LinkedHashMap(this))
    }

    private fun FirCallableSymbol<*>.toResolvedNamedReference(): FirResolvedNamedReference =
        buildResolvedNamedReference {
            this.name = this@toResolvedNamedReference.name
            resolvedSymbol = this@toResolvedNamedReference
        }

    private fun FirVariable.toResolvedNamedReference(): FirResolvedNamedReference =
        buildResolvedNamedReference {
            this.name = this@toResolvedNamedReference.name
            resolvedSymbol = this@toResolvedNamedReference.symbol
        }


//    private fun FirConstructor.generateNoArgDelegatingConstructorCall(session: FirSession) {
//        val owner = returnTypeRef.coneType.toClassSymbol(session)
//        requireNotNull(owner)
//        val delegatingConstructorCall = buildDelegatedConstructorCall {
//            val superClasses = owner.resolvedSuperTypes.filter { it.toRegularClassSymbol(session)?.classKind == ClassKind.CLASS }
//            val singleSupertype = when (superClasses.size) {
//                0 -> session.builtinTypes.anyType.coneType
//                1 -> superClasses.first()
//                else -> error("Object $owner has more than one class supertypes: $superClasses")
//            }
//            constructedTypeRef = singleSupertype.toFirResolvedTypeRef()
//            val superSymbol = singleSupertype.toRegularClassSymbol(session) ?: error("Symbol for supertype $singleSupertype not found")
//            val superConstructorSymbol = superSymbol.declaredMemberScope(session, memberRequiredPhase = null)
//                .getDeclaredConstructors()
//                .firstOrNull { it.valueParameterSymbols.isEmpty() }
//                ?: error("No arguments constructor for class $singleSupertype not found")
//            calleeReference = buildResolvedNamedReference {
//                name = superConstructorSymbol.name
//                resolvedSymbol = superConstructorSymbol
//            }
//            argumentList = FirEmptyArgumentList
//            isThis = false
//        }
//        replaceDelegatedConstructor(delegatingConstructorCall)
//    }

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
                context.owner,
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
            module.logger.log { member.renderWithType() }

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
