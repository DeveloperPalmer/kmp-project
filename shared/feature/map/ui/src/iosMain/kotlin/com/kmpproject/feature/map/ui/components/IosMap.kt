package com.kmpproject.feature.map.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kmpproject.feature.map.domain.entity.MapViewport
import com.kmpproject.feature.map.ui.entity.MapPin

@Composable
internal actual fun Map(
  pins: List<MapPin>,
  selectedPinId: Long?,
  cameraState: MapCameraState,
  onCameraIdle: (MapViewport) -> Unit,
  onPinClick: (Long) -> Unit,
  modifier: Modifier,
) = Unit
