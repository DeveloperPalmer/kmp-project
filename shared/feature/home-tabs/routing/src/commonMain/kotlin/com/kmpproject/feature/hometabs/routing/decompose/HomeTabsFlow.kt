package com.kmpproject.feature.hometabs.routing.decompose

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.pages.ChildPages
import com.arkivanov.decompose.extensions.compose.pages.PagesScrollAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kmpproject.core.ui.routing.LocalDecomposeBackHandler
import com.kmpproject.feature.cities.routing.decompose.CitiesFlow
import com.kmpproject.feature.hometabs.ui.screen.home.HomeScreen
import com.kmpproject.feature.map.routing.decompose.MapFlow

@Composable
fun HomeTabsFlow(component: HomeTabsFlowNavigationComponent) {
  val pages by component.pages.subscribeAsState()
  CompositionLocalProvider(LocalDecomposeBackHandler provides component.backHandler) {
    HomeScreen(component.viewModel) {
      ChildPages(
        pages = pages,
        onPageSelected = component::selectPage,
        scrollAnimation = PagesScrollAnimation.Default,
        pager = { modifier, state, key, pageContent ->
          HorizontalPager(
            modifier = modifier,
            key = key,
            state = state,
            userScrollEnabled = false,
            beyondViewportPageCount = 1,
            pageContent = pageContent,
          )
        },
      ) { _, page ->
        when (page) {
          is HomeTabsFlowNavigationComponent.Child.Cities -> {
            CitiesFlow(component = page.component)
          }
          is HomeTabsFlowNavigationComponent.Child.Map -> {
            MapFlow(component = page.component)
          }
        }
      }
    }
  }
}
