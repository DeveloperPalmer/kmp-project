package com.kmpproject.feature.map.domain.function

import com.kmpproject.feature.map.domain.entity.GeoPoint
import com.kmpproject.feature.map.domain.entity.MapViewport
import com.kmpproject.feature.map.domain.mapper.SETTLE_TIMEOUT
import com.kmpproject.feature.map.domain.mapper.settledAreas
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ViewportFlowsTest {
  private val viewports = MutableSharedFlow<MapViewport>(extraBufferCapacity = 1)
  private val settled = mutableListOf<MapViewport>()

  @Test
  fun `waits for the camera to settle`() = runTest {
    collectSettledAreas()

    viewports.emit(MOSCOW)
    viewports.emit(SAINT_PETERSBURG)
    advanceTimeBy(SETTLE_TIMEOUT)
    runCurrent()

    assertEquals(listOf(SAINT_PETERSBURG), settled)
  }

  @Test
  fun `skips the same area`() = runTest {
    collectSettledAreas()

    settle(MOSCOW)
    settle(MOSCOW.copy(radiusMeters = MOSCOW.radiusMeters * 1.05))

    assertEquals(listOf(MOSCOW), settled)
  }

  @Test
  fun `compares with the last requested area, so small shifts add up`() = runTest {
    collectSettledAreas()
    val shifts = (0..3).map { step ->
      MOSCOW.copy(center = GeoPoint(MOSCOW.center.lat, MOSCOW.center.lon + step * LON_STEP))
    }

    shifts.forEach { viewport -> settle(viewport) }

    assertEquals(listOf(shifts[0], shifts[2]), settled)
  }

  private fun TestScope.collectSettledAreas() {
    backgroundScope.launch { viewports.settledAreas().collect(settled::add) }
    runCurrent()
  }

  private fun TestScope.settle(viewport: MapViewport) {
    viewports.tryEmit(viewport)
    advanceTimeBy(SETTLE_TIMEOUT)
    runCurrent()
  }
}

// About 3 km at Moscow's latitude: under the 5 km tolerance of a 50 km viewport, two of them are over it
private const val LON_STEP = 0.05
private val MOSCOW = MapViewport(GeoPoint(55.7558, 37.6173), radiusMeters = 50_000.0)
private val SAINT_PETERSBURG = MapViewport(GeoPoint(59.9343, 30.3351), radiusMeters = 50_000.0)
