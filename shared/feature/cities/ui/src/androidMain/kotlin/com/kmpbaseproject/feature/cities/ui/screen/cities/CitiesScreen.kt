package com.kmpbaseproject.feature.cities.ui.screen.cities

import androidx.compose.runtime.Composable
import com.kmpbaseproject.core.ui.interop.ViewInteropMode

// Which implementation draws the screen on Android: switch it to compare them
private val interopMode = ViewInteropMode.Compose

@Composable
actual fun CitiesScreen(viewModel: CitiesViewModel) {
  when (interopMode) {
    ViewInteropMode.Compose -> {
      CitiesComposeScreen(viewModel = viewModel)
    }
    ViewInteropMode.FragmentCompose,
    ViewInteropMode.FragmentView,
    ViewInteropMode.FragmentViewBinding,
    ViewInteropMode.FragmentViewBindingInflate -> {
      // The list has no implementation in these modes: the screen fails as soon as it is created
      error("The cities screen has no $interopMode implementation on Android")
    }
  }
}
