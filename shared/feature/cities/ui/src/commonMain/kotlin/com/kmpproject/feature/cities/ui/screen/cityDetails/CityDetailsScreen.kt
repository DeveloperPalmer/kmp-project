package com.kmpproject.feature.cities.ui.screen.cityDetails

import androidx.compose.runtime.Composable

/**
 * Each platform decides how to draw the screen: iOS draws the shared Compose UI, Android may host it in a fragment,
 * which needs a FragmentActivity as the host
 */
@Composable
expect fun CityDetailsScreen(viewModel: CityDetailsViewModel)
