package com.kmpproject.feature.cities.data.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class CitiesResponse(
  val items: List<CityItem>,
  val total: Int,
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
