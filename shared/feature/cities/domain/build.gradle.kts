plugins {
  id("shared-convention")
}

kotlin {
  android {
    namespace = "com.kmpproject.feature.cities.domain"
  }
}

dependencies {
  commonMainApi(projects.shared.core.domain)

  commonMainApi(libs.paging.common)
}
