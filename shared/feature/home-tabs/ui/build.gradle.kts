plugins {
  id("compose-convention")
}

kotlin {
  android {
    namespace = "com.kmpproject.feature.hometabs.ui"
  }
}

dependencies {
  commonMainApi(projects.shared.core.uikit)
  commonMainApi(projects.shared.feature.homeTabs.domain)
}
