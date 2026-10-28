import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  kotlin("multiplatform")
  id("com.android.kotlin.multiplatform.library")
}

kotlin {
  android {
    namespace = "com.kmpproject.lib.annotation"
    compileSdk = 37
    minSdk = 26

    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  iosArm64()
  iosSimulatorArm64()
  jvm()

  jvmToolchain(17)
}

dependencies {
  commonMainImplementation(libs.kotlin.inject.runtime)
  commonMainImplementation(libs.anvil.runtime)
}
