package com.kmpproject.core.ui.routing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler

@Composable
fun DecomposeBackPressedHandler(
  enabled: Boolean,
  onBack: () -> Unit,
) {
  val handler = LocalDecomposeBackHandler.current
  val currentOnBack by rememberUpdatedState(onBack)
  val callback = remember { BackCallback(isEnabled = enabled) { currentOnBack() } }

  SideEffect { callback.isEnabled = enabled }

  DisposableEffect(handler) {
    handler.register(callback)
    onDispose { handler.unregister(callback) }
  }
}

// Each flow provides its own handler, so a callback stays silent while its flow is inactive, e.g. on a hidden tab
val LocalDecomposeBackHandler = staticCompositionLocalOf<BackHandler> {
  error("No BackHandler provided")
}
