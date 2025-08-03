package io.github.nomisrev.typedapi.compiler.plugin.fir

import org.jetbrains.kotlin.diagnostics.KtDiagnosticFactoryToRendererMap
import org.jetbrains.kotlin.diagnostics.KtDiagnosticRenderers.CLASS_ID
import org.jetbrains.kotlin.diagnostics.KtDiagnosticsContainer
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.rendering.BaseDiagnosticRendererFactory
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClass

internal object TypedApiDiagnostics : KtDiagnosticsContainer() {
    val CLASS_EXPECTED_ERROR by error1<KtClass, ClassId>()

    override fun getRendererFactory(): BaseDiagnosticRendererFactory = Errors

    private object Errors : BaseDiagnosticRendererFactory() {
        override val MAP: KtDiagnosticFactoryToRendererMap by
        KtDiagnosticFactoryToRendererMap("ExposedPlugin") {
            it.put(
                CLASS_EXPECTED_ERROR,
                "Declaration annotation with @Endpoint must be a regular class, but found {0}.",
                CLASS_ID
            )
        }
    }
}
