package com.kmpproject.feature.map.ui.function

import androidx.collection.LongObjectMap
import androidx.collection.MutableLongObjectMap
import androidx.collection.emptyLongSet
import androidx.collection.longSetOf
import com.kmpproject.feature.map.domain.entity.GeoPoint
import com.kmpproject.feature.map.ui.entity.MapPin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PinsDiffTest {
  @Test
  fun `adds every pin to an empty map`() {
    val diff = pinsDiff(shown = shown(), pins = listOf(MOSCOW, KHIMKI))

    assertEquals(listOf(MOSCOW, KHIMKI), diff.added)
    assertEquals(emptyLongSet(), diff.removedIds)
  }

  @Test
  fun `adds only new pins and leaves shown ones as they are`() {
    val diff = pinsDiff(shown = shown(MOSCOW), pins = listOf(MOSCOW, KHIMKI))

    assertEquals(listOf(KHIMKI), diff.added)
    assertEquals(emptyLongSet(), diff.removedIds)
  }

  @Test
  fun `removes pins that are gone`() {
    val diff = pinsDiff(shown = shown(MOSCOW, KHIMKI), pins = listOf(KHIMKI))

    assertEquals(emptyList(), diff.added)
    assertEquals(longSetOf(MOSCOW.id), diff.removedIds)
  }

  @Test
  fun `replaces a gone pin with a new one`() {
    val diff = pinsDiff(shown = shown(MOSCOW), pins = listOf(KHIMKI))

    assertEquals(listOf(KHIMKI), diff.added)
    assertEquals(longSetOf(MOSCOW.id), diff.removedIds)
  }

  @Test
  fun `changes nothing for the same pins`() {
    val diff = pinsDiff(shown = shown(MOSCOW, KHIMKI), pins = listOf(MOSCOW, KHIMKI))

    assertTrue(diff.isEmpty)
  }

  private fun shown(vararg pins: MapPin): LongObjectMap<MapPin> {
    return MutableLongObjectMap<MapPin>().apply {
      pins.forEach { pin -> set(pin.id, pin) }
    }
  }
}

private val MOSCOW = MapPin(id = 1, title = "Moscow", location = GeoPoint(55.75204, 37.61781))
private val KHIMKI = MapPin(id = 2, title = "Khimki", location = GeoPoint(55.89704, 37.42969))
