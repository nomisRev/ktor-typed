package io.github.nomisrev.typedapi.compiler.plugin.fir

import io.github.nomisrev.typedapi.compiler.plugin.PluginContext
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirDeclarationChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.declarations.utils.classId
import org.jetbrains.kotlin.fir.types.renderReadableWithFqNames
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.ClassId

class FirCheckers(
    session: FirSession,
    private val module: PluginContext,
) : FirAdditionalCheckersExtension(session) {
    override val declarationCheckers: DeclarationCheckers =
        object : DeclarationCheckers() {
            override val classCheckers: Set<FirClassChecker> = setOf(FirClassChecker(module))
        }
}

class FirClassChecker(private val module: PluginContext) :
    FirDeclarationChecker<FirClass>(MppCheckerKind.Common) {
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirClass) {
        val annotation =
            declaration.getAnnotationByClassId(ClassId.topLevel(module.classIds.annotation), context.session) ?: return

        module.logger.log { "annotation: ${annotation.resolvedType.renderReadableWithFqNames()}" }
        module.logger.log { "class: ${declaration.status.isData}" }
        module.logger.log { "class: ${declaration.classKind}" }
        if (declaration.status.isData) {
            reporter.reportOn(declaration.source, TypedApiDiagnostics.CLASS_EXPECTED_ERROR, declaration.classId)
            return
        }
    }
}
