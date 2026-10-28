package com.kmpproject.feature.map.ui.mapper

import com.kmpproject.feature.map.domain.entity.MapCity
import com.kmpproject.feature.map.ui.entity.MapPin

internal fun MapCity.toMapPin(): MapPin {
  return MapPin(
    id = id,
    title = name,
    location = location,
  )
}

/**
 * Pins of the cities that keep the [mapped] ones: the cities only ever gain new ones at the end,
 * so only those past [mapped] get mapped.
 */
internal fun List<MapCity>.toMapPins(mapped: List<MapPin>): List<MapPin> {
  return mapped + subList(mapped.size, size).map { city -> city.toMapPin() }
}
