package io.github.nomisrev.typedapi.compiler.plugin.fir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.containingClassForLocalAttr
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.extensions.NestedClassGenerationContext
import org.jetbrains.kotlin.fir.plugin.createCompanionObject
import org.jetbrains.kotlin.fir.plugin.createConeType
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.scopes.impl.toConeType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.ConeTypeParameterType
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.SpecialNames
import org.jetbrains.kotlin.name.StandardClassIds

class EndpointFactoryExtension(
    session: FirSession,
    module: PluginContext,
) : EndpointAnnotatedFirDeclarationGenerationExtension(session, module) {

    val create = Name.identifier("create")

    object Key : GeneratedDeclarationKey()

    /**
     *  fun <B> create(block: (path: String, (EndpointAPI) -> AnnotatedClass) -> B): B
     */
    override fun generateFunctions(
        callableId: CallableId,
        context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        val parentClass = matchedClasses.firstOrNull { it.classId == callableId.classId?.parentClassId }
        return if (callableId.callableName == create && context != null && parentClass != null) {
            listOf(
                createMemberFunction(
                    context.owner,
                    Key,
                    callableId.callableName,
                    returnTypeProvider = { typeParams -> typeParams.single().toConeType() }
                ) {
                    typeParameter(Name.identifier("B"))
                    valueParameter(Name.identifier("block"), typeProvider = { typeParams ->
                        lambdaType(parentClass, typeParams.single().toConeType())
                    })
                    status { isOverride = true }
                }.symbol
            )
        } else super.generateFunctions(callableId, context)
    }

    // (path: String, (EndpointAPI) -> AnnotatedClass) -> B
    private fun lambdaType(
        parentClass: FirRegularClassSymbol,
        typeParam: ConeTypeParameterType
    ): ConeClassLikeType {
        //  (EndpointAPI) -> AnnotatedClass
        val constructor = StandardClassIds.FunctionN(1).constructClassLikeType(
            arrayOf(
                module.classIds.api.constructClassLikeType(),
                parentClass.defaultType()
            )
        )

        // (path: String, (EndpointAPI) -> AnnotatedClass) -> B
        return StandardClassIds.FunctionN(2)
            .constructClassLikeType(arrayOf(session.builtinTypes.stringType.coneType, constructor, typeParam))
    }

    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext
    ): FirClassLikeSymbol<*>? =
        when {
            name == create -> null
            name == SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT -> {
                createCompanionObject(owner, Key) {
                    superType(module.classIds.factory.createConeType(session, arrayOf(owner.defaultType())))
                }.apply { containingClassForLocalAttr = owner.toLookupTag() }.symbol
            }

            else -> super.generateNestedClassLikeDeclaration(owner, name, context)
        }

    override fun getNestedClassifiersNames(
        classSymbol: FirClassSymbol<*>,
        context: NestedClassGenerationContext
    ): Set<Name> =
        if (!matchedClasses.contains(classSymbol)) emptySet()
        else setOf(SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT)// + Name.identifier("create")

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
        if (
            classSymbol.classId.shortClassName == SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT &&
            matchedClasses.any { it.classId == classSymbol.classId.parentClassId }
        ) {
            setOf(create)
        } else {
            super.getCallableNamesForClass(classSymbol, context)
        }
}