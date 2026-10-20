import java.util.Properties

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.plugin.compose")
  id("detekt-convention")
}

android {
  namespace = "com.kmpbaseproject.sample"
  compileSdk {
    version = release(37)
  }

  defaultConfig {
    applicationId = "com.kmpbaseproject.sample"
    minSdk = 26
    targetSdk = 37
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    val yandexApiKey = providers.environmentVariable("YANDEX_API_KEY")
      .orElse(
        providers.fileContents(rootProject.layout.projectDirectory.file("local.properties"))
          .asText
          .map { Properties().apply { load(it.reader()) }.getProperty("YANDEX_API_KEY", "") },
      )
      .getOrElse("")

    buildConfigField("String", "YANDEX_API_KEY", "\"$yandexApiKey\"")
  }

  buildTypes {
    release {
      optimization {
        enable = false
      }
    }
    create("internal") {
      initWith(getByName("release"))
      applicationIdSuffix = ".internal"
      signingConfig = signingConfigs.getByName("debug")
      // Android libraries have only debug and release: internal takes their release, as it is built on it
      matchingFallbacks += "release"
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }
}

dependencies {
  implementation(projects.shared.core.component)
  implementation(projects.shared.feature.app.routing)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.fragment.ktx)
  implementation(libs.yandex.mapkit)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}
