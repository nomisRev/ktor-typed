plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.serialization)
  id("org.jetbrains.kotlinx.kover")
}

kotlin {
  jvm()
  //  macosArm64()
  //  linuxX64()

  sourceSets {
    commonMain {
      dependencies {
        api(libs.kotlinx.serialization)
        api(libs.kotlinx.serialization.json)
        // This should be KAML, but parsing OpenAI takes 57seconds
        // Compared to 100ms with SnakeYAML
        implementation("org.yaml:snakeyaml:2.3")
      }
    }
    jvmTest { dependencies { implementation(libs.kotlin.test) } }
  }
}
