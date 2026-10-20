package com.kmpbaseproject.feature.cities.ui.screen.cityDetails

/**
 * Which implementation draws the city details screen on Android
 */
enum class CityDetailsRendering {
  /** The shared Compose UI, as on iOS */
  Compose,

  /** A fragment that hosts the shared Compose UI in a ComposeView */
  FragmentCompose,
}
