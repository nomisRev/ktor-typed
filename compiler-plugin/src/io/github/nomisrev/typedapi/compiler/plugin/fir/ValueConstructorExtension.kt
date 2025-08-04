package io.github.nomisrev.typedapi.compiler.plugin.fir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import io.github.nomisrev.typedapi.compiler.plugin.fir.HttpRequestValueExtension.Key
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.DirectDeclarationsAccess
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.expressions.FirFunctionCallOrigin
import org.jetbrains.kotlin.fir.expressions.builder.buildDelegatedConstructorCall
import org.jetbrains.kotlin.fir.expressions.builder.buildFunctionCall
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.impl.FirResolvedArgumentList
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.renderWithType
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.toFirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.ConeKotlinTypeConflictingProjection
import org.jetbrains.kotlin.fir.types.coneType
import org.jetbrains.kotlin.fir.types.constructType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.types.ConstantValueKind

// TODO what is DirectDeclarationsAccess, and can we avoid it?
class ValueConstructorExtension(
    session: FirSession,
    module: PluginContext,
) : EndpointAnnotatedFirDeclarationGenerationExtension(session, module) {

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
                argumentList = mapEndpointConstructorArguments(constructor)
            }

            // TODO remove SymbolInternals
            @OptIn(SymbolInternals::class) val primaryConstructorValues =
                primaryConstructorSymbol.valueParameterSymbols.singleOrNull()?.fir
                    ?: error("Primary Constructor requires single parameter but found: ${constructor.valueParameters.joinToString { it.name.asString() }}")
            argumentList = mapOf(mapEndpointConstructorCall to primaryConstructorValues).toResolvedArgumentList()
        })

        module.logger.log { constructor.renderWithType() }

        return listOf(constructor.symbol)
    }

    private fun mapEndpointConstructorArguments(constructor: FirConstructor): FirResolvedArgumentList =
        constructor.valueParameters.associate { param ->
            val nameToParamCall = buildFunctionCall {
                extensionReceiver = buildLiteralExpression(
                    null,
                    ConstantValueKind.String,
                    param.name.asString(),
                    setType = true
                )
                calleeReference = toSymbol.toResolvedNamedReference()
                origin = FirFunctionCallOrigin.Infix
                coneTypeOrNull = pairSymbol.constructType(
                    typeArguments = arrayOf(
                        ConeKotlinTypeConflictingProjection(session.builtinTypes.stringType.coneType),
                        ConeKotlinTypeConflictingProjection(param.returnTypeRef.coneType)
                    )
                )
                argumentList = mapOf(buildPropertyAccessExpression {
                    calleeReference = param.toResolvedNamedReference()
                    coneTypeOrNull = param.returnTypeRef.coneType
                } to param).toResolvedArgumentList()
            }
            // TODO remove SymbolInternals
            val targetParam =
                @OptIn(SymbolInternals::class) mapEndpointVarargConstructor.valueParameterSymbols.single().fir
            Pair(nameToParamCall, targetParam)
        }.toResolvedArgumentList()

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        module.logger.log { "Generating value constructor for ${classSymbol.classId}" }
        return setOf(SpecialNames.INIT)
    }
}