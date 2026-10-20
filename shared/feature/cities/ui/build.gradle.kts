plugins {
  id("compose-convention")
}

kotlin {
  android {
    namespace = "com.kmpbaseproject.feature.cities.ui"
  }
}

dependencies {
  commonMainApi(projects.shared.core.uikit)
  commonMainApi(projects.shared.feature.cities.domain)

  commonMainImplementation(libs.paging.compose)

  androidMainImplementation(libs.androidx.fragment.ktx)
  androidMainImplementation(libs.androidx.fragment.compose)
}
