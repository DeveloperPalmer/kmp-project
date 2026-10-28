package com.kmpproject.core.ui.entity

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ContentLoadState {
  @Immutable
  data object Loading : ContentLoadState

  @Immutable
  data object Ready : ContentLoadState

  @Immutable
  data class Error(val error: UiError) : ContentLoadState
}
