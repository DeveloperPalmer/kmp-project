package com.kmpproject.feature.map.ui.screen.map.screen.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.layoutId
import com.kmpproject.core.ui.safeDrawingHorizontal
import com.kmpproject.feature.map.domain.entity.MapViewport
import com.kmpproject.feature.map.ui.components.CitySheetContent
import com.kmpproject.feature.map.ui.components.Map
import com.kmpproject.feature.map.ui.components.MapBottomSheet
import com.kmpproject.feature.map.ui.components.MapCameraState
import com.kmpproject.feature.map.ui.components.MapControlsLayer
import com.kmpproject.feature.map.ui.components.ZoomControl
import com.kmpproject.feature.map.ui.components.rememberMapCameraState
import com.kmpproject.feature.map.ui.components.rememberMapControlsState
import com.kmpproject.feature.map.ui.components.rememberMapSheetState
import com.kmpproject.feature.map.ui.entity.MapControl
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MapScreen(viewModel: MapViewModel) {
  val state by viewModel.collectAsState()
  val cameraState = rememberMapCameraState()
  viewModel.collectSideEffect { sideEffect ->
    when (sideEffect) {
      SideEffect.MapAction.ZoomIn -> cameraState.zoomIn()
      SideEffect.MapAction.ZoomOut -> cameraState.zoomOut()
    }
  }
  MapContent(
    state = state,
    cameraState = cameraState,
    onPinClick = viewModel::selectCity,
    onCameraIdle = viewModel::changeViewport,
    onZoomIn = viewModel::zoomIn,
    onZoomOut = viewModel::zoomOut,
    onDismissCity = viewModel::dismissCity,
    onSearchClick = viewModel::searchCityInfo,
  )
}

@Composable
private fun MapContent(
  state: ViewState,
  cameraState: MapCameraState,
  onPinClick: (Long) -> Unit,
  onCameraIdle: (MapViewport) -> Unit,
  onZoomIn: () -> Unit,
  onZoomOut: () -> Unit,
  onDismissCity: () -> Unit,
  onSearchClick: () -> Unit,
) {
  val sheetState = rememberMapSheetState()
  val controlsState = rememberMapControlsState(listOf(sheetState))
  Box(
    modifier = Modifier
      .fillMaxSize()
      .clipToBounds(),
  ) {
    Map(
      modifier = Modifier.fillMaxSize(),
      cameraState = cameraState,
      pins = state.pins,
      selectedPinId = state.city?.id,
      onPinClick = onPinClick,
      onCameraIdle = onCameraIdle,
    )
    MapControlsLayer(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawingHorizontal),
      state = controlsState,
    ) {
      ZoomControl(
        modifier = Modifier.layoutId(MapControl.Zoom),
        visible = controlsState.isVisible(MapControl.Zoom),
        onZoomIn = onZoomIn,
        onZoomOut = onZoomOut,
      )
    }
    MapBottomSheet(
      state = sheetState,
      visible = state.city != null,
      onDismiss = onDismissCity,
    ) {
      state.city?.let { city ->
        CitySheetContent(
          city = city,
          onSearchClick = onSearchClick,
        )
      }
    }
  }
}
