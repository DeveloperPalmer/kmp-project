package com.kmpproject.core.component

import com.kmpproject.core.domain.configuration.BuildConfiguration

fun createBuildConfiguration(
  buildType: String,
  versionName: String,
): BuildConfiguration {
  return when (buildType) {
    "debug" -> BuildConfiguration.Dev(
      buildType = buildType,
      version = versionName,
    )
    "internal" -> BuildConfiguration.Internal(
      buildType = buildType,
      version = versionName,
    )
    "release" -> BuildConfiguration.Production(
      buildType = buildType,
      version = versionName,
    )
    else -> error("unknown build type name in BuildConfig: $buildType")
  }
}
