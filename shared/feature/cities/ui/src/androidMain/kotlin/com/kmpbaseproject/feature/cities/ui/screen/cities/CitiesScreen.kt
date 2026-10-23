package com.kmpbaseproject.feature.cities.ui.screen.cities

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.compose.AndroidFragment
import com.kmpbaseproject.core.ui.interop.ViewInteropMode

// Which implementation draws the screen on Android: switch it to compare them
private val interopMode = ViewInteropMode.FragmentViewBindingInflate

@Composable
actual fun CitiesScreen(viewModel: CitiesViewModel) {
  when (interopMode) {
    ViewInteropMode.Compose -> {
      CitiesComposeScreen(viewModel = viewModel)
    }
    ViewInteropMode.FragmentViewBindingInflate -> {
      AndroidFragment<CitiesFragmentViewBindingInflate>(modifier = Modifier.fillMaxSize()) { fragment ->
        fragment.onBind(viewModel)
      }
    }
    ViewInteropMode.FragmentCompose,
    ViewInteropMode.FragmentView,
    ViewInteropMode.FragmentViewBinding -> {
      // The list has no implementation in these modes: the screen fails as soon as it is created
      error("The cities screen has no $interopMode implementation on Android")
    }
  }
}
