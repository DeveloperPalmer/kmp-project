package com.kmpproject.uikit.component.textfield

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable

internal actual val WindowInsets.Companion.imeTarget: WindowInsets
  @Composable get() = WindowInsets.ime
