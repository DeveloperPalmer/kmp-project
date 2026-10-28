package com.kmpproject.uikit.component.textfield

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imeAnimationTarget
import androidx.compose.runtime.Composable

@OptIn(ExperimentalLayoutApi::class)
internal actual val WindowInsets.Companion.imeTarget: WindowInsets
  @Composable get() = WindowInsets.imeAnimationTarget
