plugins {
  id("compose-convention")
  kotlin("plugin.serialization")
}

kotlin {
  android {
    namespace = "com.kmpbaseproject.feature.map.routing"
  }
}

dependencies {
  commonMainApi(projects.shared.core.routing)
  commonMainApi(projects.shared.feature.map.data)
  commonMainApi(projects.shared.feature.map.domain)
  commonMainApi(projects.shared.feature.map.ui)

  commonMainImplementation(libs.ktor.http)
}
