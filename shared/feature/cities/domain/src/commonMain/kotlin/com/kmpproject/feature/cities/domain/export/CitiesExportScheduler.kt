package com.kmpproject.feature.cities.domain.export

import com.kmpproject.feature.cities.domain.export.ExportConstraints.RequiredNetwork
import kotlin.time.Duration

interface CitiesExportScheduler {
  fun scheduleOneTime(
    query: String,
    constraints: ExportConstraints = ExportConstraints(),
  )

  // Expedited work accepts only network and storage constraints, the rest are rejected by the platform
  fun scheduleExpedited(
    query: String,
    network: RequiredNetwork = RequiredNetwork.None,
    requiresStorageNotLow: Boolean = false,
  )

  // Intervals shorter than 15 minutes are raised to 15 minutes by the platform
  fun schedulePeriodic(
    query: String,
    interval: Duration,
    constraints: ExportConstraints = ExportConstraints(),
  )
}
