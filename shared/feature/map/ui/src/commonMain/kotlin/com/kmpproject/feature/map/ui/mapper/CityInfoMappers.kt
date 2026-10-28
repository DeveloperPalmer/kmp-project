package com.kmpproject.feature.map.ui.mapper

import com.kmpproject.feature.map.domain.entity.MapCityDetails
import com.kmpproject.feature.map.ui.entity.CityInfo

internal fun MapCityDetails.toCityInfo(): CityInfo {
  return CityInfo(
    id = id,
    name = name,
    country = country,
    population = population,
  )
}
