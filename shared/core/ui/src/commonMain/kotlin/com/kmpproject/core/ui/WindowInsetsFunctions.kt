package com.kmpproject.core.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable

val WindowInsets.Companion.safeDrawingHorizontal: WindowInsets
  @Composable
  get() = safeDrawing.only(WindowInsetsSides.Horizontal)
