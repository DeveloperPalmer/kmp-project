package com.kmpbaseproject.feature.cities.ui.screen.cityDetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.compose.AndroidFragment

// Which implementation draws the screen on Android: switch it to compare them
private val RENDERING = CityDetailsRendering.FragmentCompose

@Composable
actual fun CityDetailsScreen(viewModel: CityDetailsViewModel) {
  when (RENDERING) {
    CityDetailsRendering.Compose -> {
      CityDetailsComposeScreen(viewModel = viewModel)
    }
    CityDetailsRendering.FragmentCompose -> {
      AndroidFragment<CityDetailsFragmentCompose>(modifier = Modifier.fillMaxSize()) { fragment ->
        fragment.onBind(viewModel)
      }
    }
  }
}
