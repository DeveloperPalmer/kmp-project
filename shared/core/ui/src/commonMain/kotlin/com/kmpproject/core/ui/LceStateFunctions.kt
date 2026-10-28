package com.kmpproject.core.ui

import com.kmpproject.core.domain.entity.LceState
import com.kmpproject.core.ui.entity.ContentLoadState
import com.kmpproject.core.ui.error.ErrorMapper
import com.kmpproject.core.ui.error.baseErrorMappers

fun <T : Any> LceState<T>.toUiLceState(errorMapper: ErrorMapper = ::baseErrorMappers): ContentLoadState {
  return when (this) {
    is LceState.Content -> ContentLoadState.Ready
    is LceState.Loading -> ContentLoadState.Loading
    is LceState.Error -> ContentLoadState.Error(error = errorMapper(value))
  }
}
