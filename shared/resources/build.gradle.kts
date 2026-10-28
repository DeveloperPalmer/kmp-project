plugins {
  id("compose-convention")
}

kotlin {
  android {
    namespace = "com.kmpproject.resources"
    androidResources {
      enable = true
    }
  }
}

compose.resources {
  publicResClass = true
  packageOfResClass = "com.kmpproject.resources"
  generateResClass = always
}
