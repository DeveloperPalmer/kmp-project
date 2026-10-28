package com.kmpproject.feature.map.ui.entity

import androidx.compose.runtime.Immutable
import com.kmpproject.feature.map.domain.entity.GeoPoint

@Immutable
data class MapPin(
  val id: Long,
  val title: String,
  val location: GeoPoint,
)
