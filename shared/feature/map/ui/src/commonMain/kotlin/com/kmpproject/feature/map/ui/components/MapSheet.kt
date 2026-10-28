package com.kmpproject.feature.map.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.kmpproject.core.ui.routing.DecomposeBackPressedHandler
import com.kmpproject.core.ui.safeDrawingHorizontal
import com.kmpproject.feature.map.ui.entity.MapSheetValue
import com.kmpproject.uikit.theme.AppTheme
import com.kmpproject.uikit.theme.dropShadow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun MapBottomSheet(
  state: MapSheetState,
  visible: Boolean,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit,
) {
  val ceiling = mapCeiling()
  val scope = rememberCoroutineScope()
  val currentOnDismiss by rememberUpdatedState(onDismiss)
  val snapFlingBehavior = AnchoredDraggableDefaults.flingBehavior(state.draggable)
  val flingBehavior = remember(state, snapFlingBehavior) {
    DismissingFlingBehavior(snapFlingBehavior, state) { currentOnDismiss() }
  }

  LaunchedEffect(state, visible) {
    state.draggable.animateTo(if (visible) MapSheetValue.Expanded else MapSheetValue.Hidden)
  }

  // Dismissed only once hidden, so the content doesn't empty while the sheet slides away
  DecomposeBackPressedHandler(enabled = visible) {
    scope.launch {
      state.draggable.animateTo(MapSheetValue.Hidden)
      currentOnDismiss()
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .layout { measurable, constraints ->
        val height = constraints.maxHeight
        val placeable = measurable.measure(
          constraints.copy(
            minHeight = 0,
            maxHeight = (height - ceiling.roundToPx()).coerceAtLeast(0),
          )
        )
        // Keeping the target stops a sheet on its way up from turning back when its content grows
        state.draggable.updateAnchors(
          newAnchors = DraggableAnchors {
            MapSheetValue.Hidden at height.toFloat()
            MapSheetValue.Expanded at (height - placeable.height).toFloat()
          },
          newTarget = state.draggable.targetValue,
        )
        layout(placeable.width, height) {
          val offset = state.draggable.requireOffset().roundToInt()
          // Below the map the sheet would still cast its shadow over the map's bottom edge
          if (offset < height) {
            placeable.place(x = 0, y = offset)
          }
        }
      }
      .anchoredDraggable(
        state = state.draggable,
        orientation = Orientation.Vertical,
        flingBehavior = flingBehavior,
      )
      .dropShadow(
        shadows = AppTheme.shadows.sheet,
        shape = AppTheme.shapes.sheet
      )
      .background(
        color = AppTheme.colors.background.primary,
        shape = AppTheme.shapes.sheet
      )
      .windowInsetsPadding(WindowInsets.safeDrawingHorizontal),
  ) {
    Box(
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(top = 8.dp)
        .size(width = 48.dp, height = 4.dp)
        .background(
          color = AppTheme.colors.dragger,
          shape = AppTheme.shapes.circle
        ),
    )
    content()
  }
}

/** Snaps the sheet after a drag, like the default behavior, and dismisses it once it snaps hidden. */
@Stable
private class DismissingFlingBehavior(
  private val snap: FlingBehavior,
  private val state: MapSheetState,
  private val onDismiss: () -> Unit,
) : FlingBehavior {
  override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
    val velocityLeft = with(snap) { performFling(initialVelocity) }
    if (state.draggable.targetValue == MapSheetValue.Hidden) onDismiss()
    return velocityLeft
  }
}
