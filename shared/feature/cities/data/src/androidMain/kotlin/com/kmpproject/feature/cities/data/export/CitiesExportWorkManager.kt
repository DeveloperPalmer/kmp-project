package com.kmpproject.feature.cities.data.export

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kmpproject.core.domain.ApplicationContext
import com.kmpproject.feature.cities.domain.export.CitiesExportScheduler
import com.kmpproject.feature.cities.domain.export.ExportConstraints
import com.kmpproject.feature.cities.domain.export.ExportConstraints.RequiredNetwork
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

@Inject
@ContributesBinding(AppScope::class)
class CitiesExportWorkManager(
  @ApplicationContext
  private val context: Context,
) : CitiesExportScheduler {
  val workManager by lazy { WorkManager.getInstance(context) }

  override fun scheduleOneTime(query: String, constraints: ExportConstraints) {
    val request = OneTimeWorkRequestBuilder<CitiesExportWorker>()
      .setInputData(workDataOf(CitiesExportWorker.KEY_QUERY to query))
      .setConstraints(constraints.toWorkConstraints())
      .build()
    // A tap while an export is still pending doesn't start a second one
    workManager.enqueueUniqueWork(ONE_TIME_WORK_NAME, ExistingWorkPolicy.KEEP, request)
  }

  override fun scheduleExpedited(query: String, network: RequiredNetwork, requiresStorageNotLow: Boolean) {
    val constraints = ExportConstraints(
      network = network,
      requiresStorageNotLow = requiresStorageNotLow
    )
    val request = OneTimeWorkRequestBuilder<CitiesExportWorker>()
      .setInputData(workDataOf(CitiesExportWorker.KEY_QUERY to query))
      .setConstraints(constraints.toWorkConstraints())
      // Without quota left the work runs as a regular one instead of being dropped
      .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
      .build()
    workManager.enqueueUniqueWork(EXPEDITED_WORK_NAME, ExistingWorkPolicy.KEEP, request)
  }

  override fun schedulePeriodic(query: String, interval: Duration, constraints: ExportConstraints) {
    val request = PeriodicWorkRequestBuilder<CitiesExportWorker>(
      interval.inWholeMilliseconds,
      TimeUnit.MILLISECONDS
    )
      .setInputData(workDataOf(CitiesExportWorker.KEY_QUERY to query))
      .setConstraints(constraints.toWorkConstraints())
      .build()
    // Rescheduling applies the new query, interval and constraints to the existing work
    workManager.enqueueUniquePeriodicWork(PERIODIC_WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
  }
}

private fun ExportConstraints.toWorkConstraints(): Constraints {
  return Constraints.Builder()
    .setRequiredNetworkType(network.toNetworkType())
    .setRequiresCharging(requiresCharging)
    .setRequiresBatteryNotLow(requiresBatteryNotLow)
    .setRequiresStorageNotLow(requiresStorageNotLow)
    .setRequiresDeviceIdle(requiresDeviceIdle)
    .build()
}

private fun RequiredNetwork.toNetworkType(): NetworkType {
  return when (this) {
    RequiredNetwork.None -> NetworkType.NOT_REQUIRED
    RequiredNetwork.Connected -> NetworkType.CONNECTED
    RequiredNetwork.Unmetered -> NetworkType.UNMETERED
    RequiredNetwork.NotRoaming -> NetworkType.NOT_ROAMING
    RequiredNetwork.Metered -> NetworkType.METERED
  }
}

private const val ONE_TIME_WORK_NAME = "cities-export"
private const val EXPEDITED_WORK_NAME = "cities-export-expedited"
private const val PERIODIC_WORK_NAME = "cities-export-periodic"
