package com.kmpproject.feature.map.domain.entity

data class MapViewport(
  val center: GeoPoint,
  val radiusMeters: Double,
)
