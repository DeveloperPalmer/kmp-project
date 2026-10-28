package com.kmpproject.core.data.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@ContributesBinding(AppScope::class)
class AppWorkerFactory @Inject constructor(
  private val creators: Map<String, WorkerCreator>
) : WorkerFactory() {
  // null falls back to WorkManager's reflective creation
  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters,
  ): ListenableWorker? {
    return creators[workerClassName]?.invoke(appContext, workerParameters)
  }
}
