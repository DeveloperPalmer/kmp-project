package com.kmpproject.feature.map.routing.decompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.kmpproject.core.ui.routing.LocalDecomposeBackHandler
import com.kmpproject.feature.map.ui.screen.map.screen.map.MapScreen

@Composable
fun MapFlow(component: MapFlowNavigationComponent) {
  CompositionLocalProvider(LocalDecomposeBackHandler provides component.backHandler) {
    Children(
      stack = component.stack,
      animation = stackAnimation(slide()),
    ) { child ->
      when (val instance = child.instance) {
        is MapFlowNavigationComponent.Child.Map -> {
          MapScreen(viewModel = instance.viewModel)
        }
      }
    }
  }
}
