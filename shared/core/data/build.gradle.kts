plugins {
  id("shared-convention")
  kotlin("plugin.serialization")
  id("app.cash.sqldelight")
}

sqldelight {
  databases {
    create("CitiesDatabase") {
      srcDirs("src/commonMain/databases/cities")
      packageName.set("com.kmpproject.core.data.cities")
    }
  }
}

kotlin {
  android {
    namespace = "com.kmpproject.core.data"
  }

  sourceSets {
    androidMain.dependencies {
      api(libs.androidx.work.runtime)
      implementation(libs.ktor.okhttp)
      implementation(libs.sqldelight.android.driver)
    }
    iosMain.dependencies {
      implementation(libs.ktor.darwin)
      implementation(libs.sqldelight.native.driver)
    }
  }
}

dependencies {
  commonMainApi(projects.shared.core.domain)

  commonMainApi(libs.ktor)
  commonMainApi(libs.ktor.resources)
  commonMainImplementation(libs.ktor.content)
  commonMainImplementation(libs.ktor.json)
  commonMainImplementation(libs.ktor.logging)

  commonMainApi(libs.sqldelight.coroutines)
}
