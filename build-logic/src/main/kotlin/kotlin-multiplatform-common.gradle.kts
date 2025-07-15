plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    jvmToolchain(21)

    jvm()
// TODO report bug
//file:///Users/simonvergauwen/Developer/kotlinpoet-project-generator/build/js/packages/kotlinpoet-project-generator-generator-wasm-js-test/kotlin/kotlinpoet-project-generator-generator-wasm-js-test.uninstantiated.mjs:234
//        const wasmModule = new WebAssembly.Module(wasmBuffer);
//                           ^
//
//CompileError: WebAssembly.Module(): Compiling function #3392:"io.github.Function$buildCode$formatParametersIn..." failed: call[1] expected type (ref null 577), found struct.get of type (ref null 133) @+246621
//    at instantiate (file:///Users/simonvergauwen/Developer/kotlinpoet-project-generator/build/js/packages/kotlinpoet-project-generator-generator-wasm-js-test/kotlin/kotlinpoet-project-generator-generator-wasm-js-test.uninstantiated.mjs:234:28)
//    at async file:///Users/simonvergauwen/Developer/kotlinpoet-project-generator/build/js/packages/kotlinpoet-project-generator-generator-wasm-js-test/kotlin/kotlinpoet-project-generator-generator-wasm-js-test.mjs:6:18
//
//Node.js v22.0.0
//    wasmJs {
//        browser()
//        nodejs()
//    }

    // Tier 1
//    macosX64()
    macosArm64()
//    iosSimulatorArm64()
//    iosX64()
    // Tier 2
//    linuxX64()
    linuxArm64()
//    watchosSimulatorArm64()
//    watchosX64()
//    watchosArm32()
//    watchosArm64()
//    tvosSimulatorArm64()
//    tvosX64()
//    tvosArm64()
//    iosArm64()
    // Tier 3
//    mingwX64()
    
    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
