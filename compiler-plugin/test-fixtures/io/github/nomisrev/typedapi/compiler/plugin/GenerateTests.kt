package io.github.nomisrev.typedapi.compiler.plugin

import io.github.nomisrev.typedapi.compiler.plugin.runners.AbstractJvmBoxTest
import io.github.nomisrev.typedapi.compiler.plugin.runners.AbstractJvmDiagnosticTest
import org.jetbrains.kotlin.generators.generateTestGroupSuiteWithJUnit5

fun main() {
    generateTestGroupSuiteWithJUnit5 {
        testGroup(testDataRoot = "compiler-plugin/testData", testsRoot = "compiler-plugin/test-gen") {
            testClass<AbstractJvmDiagnosticTest> {
                model("diagnostics")
            }

            testClass<AbstractJvmBoxTest> {
                model("box")
            }
        }
    }
}
