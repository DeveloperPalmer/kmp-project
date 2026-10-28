plugins {
  id("compose-convention")
  kotlin("plugin.serialization")
}

kotlin {
  android {
    namespace = "com.kmpproject.feature.hometabs.routing"
  }
}

dependencies {
  commonMainApi(projects.shared.core.routing)
  commonMainApi(projects.shared.feature.homeTabs.domain)
  commonMainApi(projects.shared.feature.homeTabs.ui)
  commonMainApi(projects.shared.feature.cities.routing)
  commonMainApi(projects.shared.feature.map.routing)
}
