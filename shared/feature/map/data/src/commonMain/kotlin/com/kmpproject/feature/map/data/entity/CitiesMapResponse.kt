package com.kmpproject.feature.map.data.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class CitiesMapResponse(
  val items: List<CityItem>,
) {
  @Serializable
  internal data class CityItem(
    val id: Long,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val pop: Long,
  )
}
