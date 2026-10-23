// The Android Views of the cities feature: its layouts. The KMP Android plugin of cities:ui cannot
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

dependencies {
  // The binding of a layout with a RecyclerView has a field of that type, so the modules using it need the class too
  api(libs.androidx.recyclerview)

  // The layouts refer to the strings, icons and dimensions of the resources module
  implementation(projects.shared.resources)
  implementation(projects.shared.core.uikit)
}
