plugins {
  id("compose-convention")
}

kotlin {
  android {
    namespace = "com.kmpproject.core.component"
  }
}

dependencies {
  commonMainApi(projects.shared.core.data)
  commonMainApi(projects.shared.core.domain)
  commonMainApi(projects.shared.core.ui)
  commonMainApi(projects.shared.feature.app.routing)
}
