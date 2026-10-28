package com.kmpproject.feature.map.domain

import androidx.collection.MutableLongSet
import com.kmpproject.feature.map.domain.entity.MapCity

/**
 * Cities in the order they were first loaded. The model keeps them, so a subscription restarted
 * after a pause goes on with the cities loaded before instead of starting over.
 */
internal class LoadedCities {
  private val ids = MutableLongSet()
  private var cities = emptyList<MapCity>()

  /** All the cities with the new ones of [response] added at the end, or `null` when it has no new ones. */
  fun add(response: List<MapCity>): List<MapCity>? {
    val new = response.filter { city -> ids.add(city.id) }
    if (new.isEmpty()) return null
    cities = cities + new
    return cities
  }
}
