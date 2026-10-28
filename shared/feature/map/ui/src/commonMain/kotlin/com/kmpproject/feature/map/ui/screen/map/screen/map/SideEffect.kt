package com.kmpproject.feature.map.ui.screen.map.screen.map

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SideEffect {
  sealed interface MapAction : SideEffect {
    @Immutable
    data object ZoomIn : MapAction

    @Immutable
    data object ZoomOut : MapAction
  }
}
