plugins {
  id("shared-convention")
}

kotlin {
  android {
    namespace = "com.kmpproject.core.domain"
  }
}

dependencies {
  commonMainApi(libs.kotlin.coroutines.core)
  commonMainApi(libs.remo)
  commonMainApi(libs.kotlin.inject.runtime)
  commonMainApi(libs.anvil.runtime)
  commonMainApi(libs.anvil.runtime.optional)
  commonMainApi(projects.shared.lib.annotation)
}
