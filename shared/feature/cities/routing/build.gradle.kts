plugins {
  id("compose-convention")
  kotlin("plugin.serialization")
}

kotlin {
  android {
    namespace = "com.kmpbaseproject.feature.cities.routing"
  }
}

dependencies {
  commonMainApi(projects.shared.core.routing)
  commonMainApi(projects.shared.feature.cities.data)
  commonMainApi(projects.shared.feature.cities.domain)
  commonMainApi(projects.shared.feature.cities.ui)
}
