plugins {
  id("compose-convention")
}

kotlin {
  android {
    namespace = "com.kmpbaseproject.core.ui"
  }
}

dependencies {
  commonMainApi(projects.shared.core.domain)
  commonMainApi(projects.shared.resources)
  commonMainApi(libs.bundles.orbit)
  commonMainApi(libs.paging.common)
  commonMainApi(libs.androidx.lifecycle.viewmodel)
  commonMainImplementation(libs.essenty.back.handler)
}
