package com.kmpproject.core.component

import com.kmpproject.core.domain.configuration.BuildConfiguration
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
abstract class IosAppComponent(
  @get:Provides
  val buildConfiguration: BuildConfiguration,
) : AppComponent {
  abstract fun foregroundFactory(): IosForegroundComponent.Factory
}

@MergeComponent.CreateComponent
expect fun createIosAppComponent(buildConfiguration: BuildConfiguration): IosAppComponent
