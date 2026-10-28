plugins {
  id("shared-convention")
  kotlin("plugin.serialization")
}

kotlin {
  android {
    namespace = "com.kmpproject.feature.cities.data"
  }

  sourceSets {
    androidMain.dependencies {
      implementation(projects.shared.resources)
    }
  }
}

dependencies {
  commonMainApi(projects.shared.core.data)
  commonMainApi(projects.shared.feature.cities.domain)

  commonMainImplementation(libs.sqldelight.paging)
}
