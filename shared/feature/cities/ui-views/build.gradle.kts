// The Android Views of the cities feature: layouts and their strings. The KMP Android plugin of cities:ui cannot
// generate view bindings, so the layouts live in a plain Android library, which can
plugins {
  id("com.android.library")
}

android {
  namespace = "com.kmpbaseproject.feature.cities.ui.views"
  compileSdk {
    version = release(37)
  }

  defaultConfig {
    minSdk = 26
  }

  buildFeatures {
    viewBinding = true
  }
}
