package com.kmpproject.sample

import android.app.Application
import androidx.work.Configuration
import com.kmpproject.core.component.AndroidAppComponent
import com.kmpproject.core.component.AppComponentHolder
import com.kmpproject.core.component.create
import com.kmpproject.core.component.createBuildConfiguration
import com.yandex.mapkit.MapKitFactory

class ApplicationDelegate :
  Application(),
  AppComponentHolder,
  Configuration.Provider {
  private var _appComponent: AndroidAppComponent? = null

  override fun onCreate() {
    _appComponent = buildAppComponent()
    super.onCreate()
    initMapKit()
  }

  override val appComponent: AndroidAppComponent
    get() = _appComponent ?: error("app component no provided")

  // WorkManager is initialized on demand, the default initializer is removed in the manifest
  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(appComponent.workerFactory)
      .build()

  private fun buildAppComponent(): AndroidAppComponent {
    val buildConfiguration = createBuildConfiguration(
      buildType = BuildConfig.BUILD_TYPE,
      versionName = BuildConfig.VERSION_NAME,
    )
    return AndroidAppComponent::class.create(
      contextDelegate = this,
      buildConfigurationDelegate = buildConfiguration,
    )
  }

  private fun initMapKit() {
    MapKitFactory.setApiKey(BuildConfig.YANDEX_API_KEY)
    MapKitFactory.initialize(this)
  }
}
