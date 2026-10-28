package com.kmpproject.feature.app.routing.decompose

import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext
import com.kmpproject.core.routing.flow.decompose.ChildScope
import com.kmpproject.core.routing.flow.decompose.Flow
import com.kmpproject.core.routing.flow.decompose.FlowNavigationComponent
import com.kmpproject.core.routing.flow.decompose.FlowTransition
import com.kmpproject.core.routing.flow.decompose.Node
import com.kmpproject.core.routing.flow.decompose.Screen
import com.kmpproject.core.ui.routing.Event
import com.kmpproject.feature.app.routing.AppFlowComponent
import com.kmpproject.feature.app.routing.decompose.AppFlowNavigationComponent.Child
import com.kmpproject.feature.app.routing.decompose.AppFlowNavigationComponent.Config
import com.kmpproject.feature.cities.ui.routing.FlowEvent
import com.kmpproject.feature.cities.ui.screen.cityDetails.CityDetailsViewModel
import com.kmpproject.feature.hometabs.routing.decompose.HomeTabsFlowNavigationComponent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Stable
class AppFlowNavigationComponent(
  context: ComponentContext,
  override val component: AppFlowComponent,
) : FlowNavigationComponent<Config, Child>(context) {
  override val configSerializer: KSerializer<Config> = Config.serializer()

  override fun initialConfig(): List<Config> = listOf(Config.Home)

  override fun transition(event: Event): FlowTransition<Config> {
    return when (event) {
      is FlowEvent.CityDetailsRequested -> {
        FlowTransition.NavigateTo(Config.CityDetails(event.cityId))
      }
      is FlowEvent.CityDetailsDismissed -> {
        FlowTransition.Back
      }
      else -> {
        FlowTransition.Ignore
      }
    }
  }

  override val childFactory: ChildScope.(Config) -> Child = { config ->
    when (config) {
      is Config.Home -> {
        Child.Home(HomeTabsFlowNavigationComponent(context, flowComponent { component.homeTabsFlowComponent() }))
      }
      is Config.CityDetails -> {
        Child.CityDetails(viewModel<CityDetailsViewModel>(config.cityId))
      }
    }
  }

  @Serializable
  sealed interface Config {
    @Serializable
    data object Home : Config

    @Serializable
    data class CityDetails(val cityId: Long) : Config
  }

  sealed interface Child : Node {
    data class Home(override val component: HomeTabsFlowNavigationComponent) : Child, Flow
    data class CityDetails(override val viewModel: CityDetailsViewModel) : Child, Screen
  }
}
