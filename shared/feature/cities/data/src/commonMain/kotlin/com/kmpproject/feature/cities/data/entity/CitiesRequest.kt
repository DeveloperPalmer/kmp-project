package com.kmpproject.feature.cities.data.entity

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("cities")
internal data class CitiesRequest(
  val query: String,
  val page: Int,
  val limit: Int,
)
