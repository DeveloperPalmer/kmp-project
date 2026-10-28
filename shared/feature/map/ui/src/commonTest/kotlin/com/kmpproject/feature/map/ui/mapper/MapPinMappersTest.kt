package com.kmpproject.feature.map.ui.mapper

import com.kmpproject.feature.map.domain.entity.GeoPoint
import com.kmpproject.feature.map.domain.entity.MapCity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class MapPinMappersTest {
  @Test
  fun `maps every city while no pins are mapped`() {
    val pins = listOf(MOSCOW, KHIMKI).toMapPins(mapped = emptyList())

    assertEquals(listOf(MOSCOW.toMapPin(), KHIMKI.toMapPin()), pins)
  }

  @Test
  fun `keeps mapped pins and maps only the cities after them`() {
    val mapped = listOf(MOSCOW.toMapPin())

    val pins = listOf(MOSCOW, KHIMKI).toMapPins(mapped)

    assertEquals(listOf(MOSCOW.toMapPin(), KHIMKI.toMapPin()), pins)
    assertSame(mapped.single(), pins.first())
  }
}

private val MOSCOW = MapCity(id = 1, name = "Moscow", location = GeoPoint(55.75204, 37.61781))
private val KHIMKI = MapCity(id = 2, name = "Khimki", location = GeoPoint(55.89704, 37.42969))
