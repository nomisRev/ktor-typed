package io.github.nomisrev.typedapi.compiler.plugin.fir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.ExperimentalTopLevelDeclarationsGenerationApi
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirDeclarationPredicateRegistrar
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.plugin.createConstructor
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.name.callableIdForConstructor

class MyCodeGenerationExtension(
    session: FirSession,
    private val module: PluginContext,
) : FirDeclarationGenerationExtension(session) {
    private val predicate = LookupPredicate.create { annotated(module.classIds.annotation) }
    private val predicateBasedProvider = session.predicateBasedProvider
    private val matchedClasses by lazy {
        predicateBasedProvider.getSymbolsByPredicate(predicate).filterIsInstance<FirRegularClassSymbol>()
    }

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(predicate)
    }

    object Key : GeneratedDeclarationKey()

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val endpoint = context.declaredScope?.classId?.let { classId -> matchedClasses.find { it.classId == classId } }
            ?: return emptyList()
        val properties = endpoint.declarationSymbols.filterIsInstance<FirPropertySymbol>()
        val constructor = createConstructor(endpoint, Key) {
            properties.forEach { prop ->
                valueParameter(prop.name, prop.resolvedReturnType)
            }
        }
        module.logger.log { "matchedClasses: ${matchedClasses.map { it.classId }}, and adding ${constructor.valueParameters.joinToString { it.name.asString() }}" }
        return listOf(constructor.symbol)
    }

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun generateTopLevelClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? {
        module.logger.log { "matchedClasses: ${matchedClasses.map { it.classId }}" }
        return super.generateTopLevelClassLikeDeclaration(classId)
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
        if (matchedClasses.contains(classSymbol)) setOf(SpecialNames.INIT)
        else super.getCallableNamesForClass(classSymbol, context)

    @ExperimentalTopLevelDeclarationsGenerationApi
    override fun getTopLevelClassIds(): Set<ClassId> =
        matchedClasses.mapTo(mutableSetOf()) {
            it.classId
        }
}

fun ClassId.defaultType(): ConeClassLikeType = defaultType(emptyList())
