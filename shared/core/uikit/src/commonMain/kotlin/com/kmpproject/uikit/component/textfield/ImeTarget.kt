package com.kmpproject.uikit.component.textfield

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable

// IME insets at the end of the running animation: unlike WindowInsets.ime they don't jump between frames.
internal expect val WindowInsets.Companion.imeTarget: WindowInsets
  @Composable get
