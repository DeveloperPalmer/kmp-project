package com.kmpproject.feature.map.ui.components

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.kmpproject.feature.map.ui.entity.MapObstacle
import com.kmpproject.feature.map.ui.entity.MapSheetValue

@Composable
internal fun rememberMapSheetState(): MapSheetState {
  return remember { MapSheetState() }
}

@Stable
internal class MapSheetState : MapObstacle {
  val draggable = AnchoredDraggableState(MapSheetValue.Hidden)

  override val top: Float
    get() {
      val offset = draggable.offset
      return if (offset.isNaN()) Float.POSITIVE_INFINITY else offset
    }
}
