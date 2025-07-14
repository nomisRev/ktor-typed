package io.github.nomisrev.typedapi.compiler.plugin.fir

import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1DelegateProvider
import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticRenderers.CLASS_ID
import org.jetbrains.kotlin.diagnostics.Severity
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.diagnostics.rendering.RootDiagnosticRendererFactory
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClass

object Errors : BaseDiagnosticRendererFactory() {

    val CLASS_EXPECTED by DiagnosticFactory1DelegateProvider<ClassId>(
        severity = Severity.ERROR,
        positioningStrategy = SourceElementPositioningStrategies.DEFAULT,
        psiType = KtClass::class,
    )

    @Suppress("ktlint:standard:property-naming")
    override val MAP: KtDiagnosticFactoryToRendererMap =
        KtDiagnosticFactoryToRendererMap("ExposedPlugin").apply {
            put(
                CLASS_EXPECTED,
                "Declaration annotation with @Endpoint must be a regular class, but found {0}.",
                CLASS_ID,
            )
        }

    init {
        RootDiagnosticRendererFactory.registerFactory(this)
    }
}
