package com.kmpproject.feature.map.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.Padding
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.mapview.MapView

@Composable
internal fun MapLogoEffect(mapView: MapView) {
  val density = LocalDensity.current
  val top = with(density) { mapCeiling().roundToPx() }
  val end = with(density) { LOGO_END_PADDING.roundToPx() }
  LaunchedEffect(mapView, top, end) {
    mapView.mapWindow.map.logo.apply {
      setAlignment(Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP))
      setPadding(Padding(end, top))
    }
  }
}

private val LOGO_END_PADDING = 12.dp
