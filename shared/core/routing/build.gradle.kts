plugins {
  id("compose-convention")
  kotlin("plugin.serialization")
}

kotlin {
  android {
    namespace = "com.kmpproject.core.routing"
    withHostTest {}
  }

  sourceSets {
    getByName("androidHostTest") {
      // kctfork API is marked with the Kotlin compiler's experimental marker
      languageSettings.optIn("org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")

      dependencies {
        implementation(libs.bundles.compile.test)
        implementation(projects.lib.generator)
      }
    }
  }
}

dependencies {
  commonMainApi(libs.bundles.decompose)
  commonMainApi(projects.shared.core.ui)
  commonTestImplementation(libs.bundles.unit.test)
}
