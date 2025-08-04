package io.github.nomisrev.typedapi.compiler.plugin.fir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.containingClassForStaticMemberAttr
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.extensions.MemberGenerationContext
import org.jetbrains.kotlin.fir.plugin.createMemberFunction
import org.jetbrains.kotlin.fir.renderWithType
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.constructClassLikeType
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly

internal class HttpRequestValueExtension(
    session: FirSession,
    module: PluginContext,
) : EndpointAnnotatedFirDeclarationGenerationExtension(session, module) {
    companion object {
        /** The identifiers of [io.github.nomisrev.typedapi.HttpRequestValue]. */
        val callableNames = setOf(
            Name.identifier("query"),
            Name.identifier("path"),
            Name.identifier("header"),
            Name.identifier("body"),
        )
    }

    object Key : GeneratedDeclarationKey()

    /** Generates [function] and [pathStringOrNull] for each endpoint. */
    override fun generateFunctions(
        callableId: CallableId, context: MemberGenerationContext?
    ): List<FirNamedFunctionSymbol> {
        val endpoint = context?.declaredScope?.classId
            ?.let { classId -> matchedClasses.find { it.classId == classId } } ?: return emptyList()
        if (!callableNames.contains(callableId.callableName)) return emptyList()
        val name = callableId.callableName.asString().capitalizeAsciiOnly()
        val inputType = module.classIds.input.createNestedClassId(Name.identifier(name))

        val member = function(inputType, context, callableId, endpoint)
        val pathOrNull = pathStringOrNull(callableId, endpoint)
        module.logger.log { "Generating HttpRequestValue for $endpoint.${member.name}" }

        return listOfNotNull(member.symbol, pathOrNull?.symbol)
    }

    /**
     * Generates HttpRequestValue visitors.
     *
     * - override fun query(block: (value: Any?, input: Input.Query<Any?>) -> Unit): Unit
     * - override fun path(block: (value: Any?, input: Input.Path<Any?>) -> Unit): Unit
     * - override fun header(block: (value: Any?, input: Input.Header<Any?>) -> Unit): Unit
     * - override fun body(block: (value: Any?, input: Input.Body<Any?>) -> Unit): Unit
     */
    private fun function(
        inputType: ClassId,
        context: MemberGenerationContext,
        callableId: CallableId,
        endpoint: FirRegularClassSymbol
    ): FirSimpleFunction {
        val lambdaType = StandardClassIds.FunctionN(2).constructClassLikeType(
            arrayOf(
                session.builtinTypes.nullableAnyType.coneType,
                inputType.constructClassLikeType(typeArguments = arrayOf(session.builtinTypes.nullableAnyType.coneType)),
                session.builtinTypes.unitType.coneType
            )
        )
        return createMemberFunction(
            owner = context.owner,
            key = Key,
            name = callableId.callableName,
            returnType = session.builtinTypes.unitType.coneType
        ) {
            valueParameter(Name.identifier("block"), lambdaType)
            status { isOverride = true }
        }.apply { containingClassForStaticMemberAttr = endpoint.toLookupTag() }
    }

    /** Generates `override fun path(): String`, or `null` if `callableId` != `path` */
    private fun pathStringOrNull(callableId: CallableId, endpoint: FirRegularClassSymbol): FirSimpleFunction? =
        if (callableId.callableName.asString() == "path") {
            createMemberFunction(endpoint, Key, callableId.callableName, session.builtinTypes.stringType.coneType) {
                status { isOverride = true }
            }.apply { containingClassForStaticMemberAttr = endpoint.toLookupTag() }
        } else null

    /**
     * We generate a special constructor for HttpRequestValue, and its interface functions
     */
    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> =
        if (matchedClasses.contains(classSymbol)) {
            module.logger.log { "Generating HttpRequestValue implementation for ${classSymbol.classId}." }
            callableNames
        } else emptySet()
}
