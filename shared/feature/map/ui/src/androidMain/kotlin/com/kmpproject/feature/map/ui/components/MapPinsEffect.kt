package com.kmpproject.feature.map.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.kmpproject.feature.map.ui.entity.MapPin
import com.yandex.mapkit.mapview.MapView

@Composable
internal fun MapPinsEffect(
  mapView: MapView,
  pins: List<MapPin>,
  selectedPinId: Long?,
  onPinClick: (Long) -> Unit
) {
  val images = rememberPinImages()
  val currentOnPinClick by rememberUpdatedState(onPinClick)
  val layer = remember(mapView, images) {
    MapPinsLayer(mapView.mapWindow.map, images) { id -> currentOnPinClick(id) }
  }
  DisposableEffect(layer) {
    onDispose { layer.clear() }
  }
  LaunchedEffect(layer, pins) {
    layer.show(pins)
  }
  LaunchedEffect(layer, selectedPinId) {
    layer.select(selectedPinId)
  }
}
