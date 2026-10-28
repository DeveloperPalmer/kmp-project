package com.kmpproject.feature.app.routing.decompose

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.kmpproject.feature.cities.ui.screen.cityDetails.CityDetailsScreen
import com.kmpproject.feature.hometabs.routing.decompose.HomeTabsFlow

@Composable
fun AppFlow(component: AppFlowNavigationComponent) {
  Children(
    stack = component.stack,
    animation = stackAnimation(slide()),
  ) { child ->
    when (val instance = child.instance) {
      is AppFlowNavigationComponent.Child.Home -> {
        HomeTabsFlow(component = instance.component)
      }
      is AppFlowNavigationComponent.Child.CityDetails -> {
        CityDetailsScreen(viewModel = instance.viewModel)
      }
    }
  }
}
