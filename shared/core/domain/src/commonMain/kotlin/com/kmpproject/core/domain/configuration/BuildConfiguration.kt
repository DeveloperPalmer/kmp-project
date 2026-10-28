package com.kmpproject.core.domain.configuration

sealed interface BuildConfiguration {
  val buildType: String
  val version: String

  data class Dev(
    override val buildType: String,
    override val version: String,
  ) : BuildConfiguration

  data class Internal(
    override val buildType: String,
    override val version: String,
  ) : BuildConfiguration

  data class Production(
    override val buildType: String,
    override val version: String,
  ) : BuildConfiguration
}
