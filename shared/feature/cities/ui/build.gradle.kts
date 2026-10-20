plugins {
  id("compose-convention")
}

kotlin {
  android {
    namespace = "com.kmpbaseproject.feature.cities.ui"

    // The View fragment of the city details screen has an XML layout and Android strings
    androidResources {
      enable = true
    }
  }
}

dependencies {
  commonMainApi(projects.shared.core.uikit)
  commonMainApi(projects.shared.feature.cities.domain)

  commonMainImplementation(libs.paging.compose)

  androidMainImplementation(libs.androidx.fragment.ktx)
  androidMainImplementation(libs.androidx.fragment.compose)
}
