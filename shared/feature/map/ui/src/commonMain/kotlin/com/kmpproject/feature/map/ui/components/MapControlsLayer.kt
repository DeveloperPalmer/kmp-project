package com.kmpproject.feature.map.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.kmpproject.feature.map.ui.entity.MapControl
import kotlin.math.roundToInt

/**
 * Layer of the map controls, each child tagged with its [MapControl] via `layoutId`.
 *
 * Children are measured with unbounded constraints, so an obstacle never squeezes them, and
 * [MapControlsState.topOf] is read only at placement, so a moving obstacle doesn't recompose them.
 */
@Composable
internal fun MapControlsLayer(
  state: MapControlsState,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  Layout(
    modifier = modifier,
    content = content,
  ) { measurables, constraints ->
    val placeables = measurables.map { measurable ->
      val control = measurable.layoutId as? MapControl ?: error("Map control without MapControl layoutId")
      val placeable = measurable.measure(Constraints())
      state.onMeasured(control, placeable.height)
      placeable
    }
    layout(constraints.maxWidth, constraints.maxHeight) {
      placeables.forEach { placeable ->
        placeable.placeRelative(
          x = constraints.maxWidth - placeable.width - END_PADDING.roundToPx(),
          y = state.topOf(placeable.height, constraints.maxHeight).roundToInt(),
        )
      }
    }
  }
}

private val END_PADDING = 12.dp
