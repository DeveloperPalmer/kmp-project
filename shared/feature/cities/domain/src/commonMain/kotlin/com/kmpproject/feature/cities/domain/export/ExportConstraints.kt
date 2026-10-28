package com.kmpproject.feature.cities.domain.export

data class ExportConstraints(
  val network: RequiredNetwork = RequiredNetwork.None,
  val requiresCharging: Boolean = false,
  val requiresBatteryNotLow: Boolean = false,
  val requiresStorageNotLow: Boolean = false,
  val requiresDeviceIdle: Boolean = false,
) {
  enum class RequiredNetwork {
    None,
    Connected,
    Unmetered,
    NotRoaming,
    Metered,
  }
}
