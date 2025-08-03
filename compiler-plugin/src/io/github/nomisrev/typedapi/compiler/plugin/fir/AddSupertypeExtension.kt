package io.github.nomisrev.typedapi.compiler.plugin.fir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.extensions.FirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.fqName
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef

class AddSupertypeExtension(session: FirSession, module: PluginContext) : FirSupertypeGenerationExtension(session) {
    private val annotation = module.classIds.annotation
    private val httpRequestValue = module.classIds.httpRequestValue.defaultType(emptyList())

    override fun needTransformSupertypes(declaration: FirClassLikeDeclaration): Boolean =
        declaration.annotations.any { it.fqName(session) == annotation }

    override fun computeAdditionalSupertypes(
        classLikeDeclaration: FirClassLikeDeclaration,
        resolvedSupertypes: List<FirResolvedTypeRef>,
        typeResolver: TypeResolveService
    ): List<ConeKotlinType> = listOf(httpRequestValue)
}
