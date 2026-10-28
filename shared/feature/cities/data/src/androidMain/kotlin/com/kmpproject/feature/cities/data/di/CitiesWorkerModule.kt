package com.kmpproject.feature.cities.data.di

import android.content.Context
import androidx.work.WorkerParameters
import com.kmpproject.core.data.work.WorkerCreator
import com.kmpproject.feature.cities.data.export.CitiesExportWorker
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface CitiesWorkerModule {
  @Provides
  @IntoMap
  fun provideCitiesExportWorker(
    create: (Context, WorkerParameters) -> CitiesExportWorker
  ): Pair<String, WorkerCreator> {
    return CitiesExportWorker::class.java.name to create
  }
}
