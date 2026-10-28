package com.kmpproject.feature.map.domain.entity

data class MapCity(
  val id: Long,
  val name: String,
  val location: GeoPoint,
)
