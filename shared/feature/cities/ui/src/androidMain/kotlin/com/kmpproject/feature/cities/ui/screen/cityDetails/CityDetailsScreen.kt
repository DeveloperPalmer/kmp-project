package com.kmpproject.feature.cities.ui.screen.cityDetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.compose.AndroidFragment
import com.kmpproject.core.ui.interop.ViewInteropMode

// Which implementation draws the screen on Android: switch it to compare them
private val interopMode = ViewInteropMode.FragmentViewBindingInflate

@Composable
actual fun CityDetailsScreen(viewModel: CityDetailsViewModel) {
  when (interopMode) {
    ViewInteropMode.Compose -> {
      CityDetailsComposeScreen(viewModel = viewModel)
    }
    ViewInteropMode.FragmentCompose -> {
      AndroidFragment<CityDetailsFragmentCompose>(modifier = Modifier.fillMaxSize()) { fragment ->
        fragment.onBind(viewModel)
      }
    }
    ViewInteropMode.FragmentView -> {
      AndroidFragment<CityDetailsFragmentView>(modifier = Modifier.fillMaxSize()) { fragment ->
        fragment.onBind(viewModel)
      }
    }
    ViewInteropMode.FragmentViewBinding -> {
      AndroidFragment<CityDetailsFragmentViewBinding>(modifier = Modifier.fillMaxSize()) { fragment ->
        fragment.onBind(viewModel)
      }
    }
    ViewInteropMode.FragmentViewBindingInflate -> {
      AndroidFragment<CityDetailsFragmentViewBindingInflate>(modifier = Modifier.fillMaxSize()) { fragment ->
        fragment.onBind(viewModel)
      }
    }
  }
}
