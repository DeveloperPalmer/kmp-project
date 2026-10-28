package com.kmpproject.feature.map.data.entity

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("cities/map")
internal data class CitiesMapRequest(
  val centerLat: Double,
  val centerLng: Double,
  val radius: Int,
)
