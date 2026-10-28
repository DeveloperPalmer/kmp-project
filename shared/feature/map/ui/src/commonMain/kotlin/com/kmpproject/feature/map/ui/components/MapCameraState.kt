package com.kmpproject.feature.map.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@Composable
internal fun rememberMapCameraState(): MapCameraState {
  return remember { MapCameraState() }
}

@Stable
internal class MapCameraState {
  // Bound by the platform map while it is in composition
  var zoomBy: ((Float) -> Unit)? = null

  fun zoomIn() {
    zoomBy?.invoke(1f)
  }

  fun zoomOut() {
    zoomBy?.invoke(-1f)
  }
}
