plugins {
  id("compose-convention")
}

kotlin {
  android {
    namespace = "com.kmpproject.feature.map.ui"
    withHostTest {}
  }

  sourceSets {
    androidMain.dependencies {
      implementation(libs.yandex.mapkit)
    }
  }
}

dependencies {
  commonMainApi(projects.shared.core.uikit)
  commonMainApi(projects.shared.feature.map.domain)
  commonTestImplementation(libs.bundles.unit.test)
}
