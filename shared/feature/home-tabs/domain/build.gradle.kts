plugins {
  id("shared-convention")
}

kotlin {
  android {
    namespace = "com.kmpproject.feature.hometabs.domain"
  }
}

dependencies {
  commonMainApi(projects.shared.core.domain)
}
