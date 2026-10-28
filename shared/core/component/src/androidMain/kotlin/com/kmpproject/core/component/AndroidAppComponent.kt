package com.kmpproject.core.component

import android.content.Context
import androidx.work.WorkerFactory
import com.kmpproject.core.domain.ApplicationContext
import com.kmpproject.core.domain.configuration.BuildConfiguration
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
abstract class AndroidAppComponent(
  @get:Provides
  @ApplicationContext
  val context: Context,
  @get:Provides
  val buildConfiguration: BuildConfiguration,
) : AppComponent {
  abstract val workerFactory: WorkerFactory

  abstract fun foregroundComponentFactory(): AndroidForegroundComponent.Factory
}
