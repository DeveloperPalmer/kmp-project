package com.kmpproject.feature.map.domain.function

import com.kmpproject.feature.map.domain.entity.GeoPoint
import com.kmpproject.feature.map.domain.entity.MapViewport
import com.kmpproject.feature.map.domain.mapper.distanceMeters
import com.kmpproject.feature.map.domain.mapper.isSameArea
import com.kmpproject.feature.map.domain.mapper.viewportAround
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeoFunctionsTest {
  @Test
  fun `measures the distance between Moscow and Saint Petersburg`() {
    assertEquals(633_020.0, distanceMeters(MOSCOW, SAINT_PETERSBURG), absoluteTolerance = 1.0)
  }

  @Test
  fun `measures a degree of longitude on the equator`() {
    assertEquals(111_195.0, distanceMeters(GeoPoint(0.0, 0.0), GeoPoint(0.0, 1.0)), absoluteTolerance = 1.0)
  }

  @Test
  fun `reaches the farthest corner`() {
    val center = GeoPoint(0.0, 0.0)
    val corners = listOf(GeoPoint(0.0, 1.0), GeoPoint(0.0, -2.0), GeoPoint(1.0, 0.0))

    val viewport = viewportAround(center, corners)

    assertEquals(center, viewport.center)
    assertEquals(distanceMeters(center, GeoPoint(0.0, -2.0)), viewport.radiusMeters)
  }

  @Test
  fun `treats a small shift of the camera as the same area`() {
    val shifted = VIEWPORT.copy(center = GeoPoint(MOSCOW.lat, MOSCOW.lon + 0.05))

    assertTrue(VIEWPORT.isSameArea(shifted))
  }

  @Test
  fun `treats a shift beyond the tolerance as another area`() {
    val shifted = VIEWPORT.copy(center = GeoPoint(MOSCOW.lat, MOSCOW.lon + 0.2))

    assertFalse(VIEWPORT.isSameArea(shifted))
  }

  @Test
  fun `treats zooming in as another area`() {
    val zoomedIn = VIEWPORT.copy(radiusMeters = VIEWPORT.radiusMeters / 2)

    assertFalse(VIEWPORT.isSameArea(zoomedIn))
  }
}

private val MOSCOW = GeoPoint(55.7558, 37.6173)
private val SAINT_PETERSBURG = GeoPoint(59.9343, 30.3351)
private val VIEWPORT = MapViewport(MOSCOW, radiusMeters = 50_000.0)
