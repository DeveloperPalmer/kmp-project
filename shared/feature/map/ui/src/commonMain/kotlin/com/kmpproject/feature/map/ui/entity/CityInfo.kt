package com.kmpproject.feature.map.ui.entity

import androidx.compose.runtime.Immutable

@Immutable
data class CityInfo(
  val id: Long,
  val name: String,
  val country: String,
  val population: Long,
)
