package com.kmpproject.feature.map.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun mapCeiling(): Dp {
  return WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + CEILING_MARGIN
}

private val CEILING_MARGIN = 12.dp
