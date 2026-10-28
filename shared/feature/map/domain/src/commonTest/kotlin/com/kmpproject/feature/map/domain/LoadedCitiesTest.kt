package com.kmpproject.feature.map.domain

import com.kmpproject.feature.map.domain.entity.GeoPoint
import com.kmpproject.feature.map.domain.entity.MapCity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LoadedCitiesTest {
  @Test
  fun `adds new cities after the ones loaded before`() {
    val loadedCities = LoadedCities()
    loadedCities.add(listOf(MOSCOW))

    assertEquals(listOf(MOSCOW, KHIMKI), loadedCities.add(listOf(KHIMKI, MOSCOW)))
  }

  @Test
  fun `adds nothing for a response without new cities`() {
    val loadedCities = LoadedCities()
    loadedCities.add(listOf(MOSCOW, KHIMKI))

    assertNull(loadedCities.add(listOf(KHIMKI)))
  }
}

private val MOSCOW = MapCity(id = 1, name = "Moscow", location = GeoPoint(55.75204, 37.61781))
private val KHIMKI = MapCity(id = 2, name = "Khimki", location = GeoPoint(55.89704, 37.42969))
