package com.kmpproject.feature.map.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.kmpproject.feature.map.ui.entity.MapControl
import com.kmpproject.feature.map.ui.entity.MapObstacle

@Composable
internal fun rememberMapControlsState(obstacles: List<MapObstacle>): MapControlsState {
  val density = LocalDensity.current
  val ceiling = mapCeiling()
  return remember(density, ceiling, obstacles) {
    with(density) {
      MapControlsState(
        obstacles = obstacles,
        ceiling = ceiling.toPx(),
        pushGap = PUSH_GAP.toPx(),
        hideGap = HIDE_GAP.toPx(),
      )
    }
  }
}

/**
 * Where the map controls stand and whether they are shown, derived from what covers the map.
 *
 * Controls sit centered; the nearest obstacle pushes them up to keep [pushGap] until they reach
 * [ceiling], and once the gap left there is [hideGap] or less they hide. All values are px of the
 * map layer, so the holder needs no Compose to be tested.
 */
@Stable
internal class MapControlsState(
  private val obstacles: List<MapObstacle>,
  private val ceiling: Float,
  private val pushGap: Float,
  private val hideGap: Float,
) {
  private val heights = mutableStateMapOf<MapControl, Int>()

  private val visibility: Map<MapControl, State<Boolean>> = MapControl.entries.associateWith { control ->
    derivedStateOf { floor() - ceiling - heightOf(control) > hideGap }
  }

  fun isVisible(control: MapControl): Boolean {
    return visibility.getValue(control).value
  }

  fun topOf(height: Int, viewportHeight: Int): Float {
    val centered = (viewportHeight - height) / 2f
    val pushed = floor() - pushGap - height
    return maxOf(ceiling, minOf(centered, pushed))
  }

  // A hidden control measures 0; remembering that would show it again right away.
  fun onMeasured(control: MapControl, height: Int) {
    if (height > 0 && heights[control] != height) {
      heights[control] = height
    }
  }

  private fun heightOf(control: MapControl): Int {
    return heights[control] ?: 0
  }

  // Read on every frame of a drag, so it neither allocates nor boxes
  private fun floor(): Float {
    var floor = Float.POSITIVE_INFINITY
    for (index in obstacles.indices) {
      floor = minOf(floor, obstacles[index].top)
    }
    return floor
  }
}

private val PUSH_GAP = 96.dp
private val HIDE_GAP = 16.dp
