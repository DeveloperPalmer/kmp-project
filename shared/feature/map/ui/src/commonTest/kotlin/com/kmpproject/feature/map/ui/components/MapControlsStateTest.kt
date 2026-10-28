package com.kmpproject.feature.map.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import com.kmpproject.feature.map.ui.entity.MapControl
import com.kmpproject.feature.map.ui.entity.MapObstacle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapControlsStateTest {
  @Test
  fun `stands centered without obstacles`() {
    val state = controlsState(sheetTop = Float.POSITIVE_INFINITY)

    assertEquals(450f, state.topOf(HEIGHT, VIEWPORT))
    assertTrue(state.isVisible(MapControl.Zoom))
  }

  @Test
  fun `stays centered while the sheet is farther than the push gap`() {
    val state = controlsState(sheetTop = 900f)

    assertEquals(450f, state.topOf(HEIGHT, VIEWPORT))
  }

  @Test
  fun `is pushed up keeping the push gap to the sheet`() {
    val state = controlsState(sheetTop = 600f)

    assertEquals(404f, state.topOf(HEIGHT, VIEWPORT))
  }

  @Test
  fun `stops at the ceiling`() {
    val state = controlsState(sheetTop = 200f)

    assertEquals(CEILING, state.topOf(HEIGHT, VIEWPORT))
    assertTrue(state.isVisible(MapControl.Zoom))
  }

  @Test
  fun `hides when the gap at the ceiling shrinks to the hide gap and shows again above it`() {
    val sheet = FakeObstacle(top = 166f)
    val state = controlsState(sheet)

    assertFalse(state.isVisible(MapControl.Zoom))

    sheet.top = 167f
    assertTrue(state.isVisible(MapControl.Zoom))
  }

  @Test
  fun `follows the nearest obstacle`() {
    val state = MapControlsState(
      obstacles = listOf(FakeObstacle(top = 900f), FakeObstacle(top = 600f)),
      ceiling = CEILING,
      pushGap = PUSH_GAP,
      hideGap = HIDE_GAP,
    ).measured()

    assertEquals(404f, state.topOf(HEIGHT, VIEWPORT))
  }

  @Test
  fun `keeps the last height when a hidden control measures zero`() {
    val state = controlsState(sheetTop = 166f)

    state.onMeasured(MapControl.Zoom, 0)

    assertFalse(state.isVisible(MapControl.Zoom))
  }

  private fun controlsState(sheetTop: Float): MapControlsState {
    return controlsState(FakeObstacle(sheetTop))
  }

  private fun controlsState(sheet: MapObstacle): MapControlsState {
    return MapControlsState(
      obstacles = listOf(sheet),
      ceiling = CEILING,
      pushGap = PUSH_GAP,
      hideGap = HIDE_GAP,
    ).measured()
  }

  private fun MapControlsState.measured(): MapControlsState {
    return apply { onMeasured(MapControl.Zoom, HEIGHT) }
  }

  private class FakeObstacle(top: Float) : MapObstacle {
    override var top: Float by mutableFloatStateOf(top)
  }
}

private const val VIEWPORT = 1000
private const val HEIGHT = 100
private const val CEILING = 50f
private const val PUSH_GAP = 96f
private const val HIDE_GAP = 16f
