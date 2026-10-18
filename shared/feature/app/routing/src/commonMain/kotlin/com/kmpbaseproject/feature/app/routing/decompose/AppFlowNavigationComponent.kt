package com.kmpbaseproject.feature.app.routing.decompose

import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext
import com.kmpbaseproject.core.routing.flow.decompose.Flow
import com.kmpbaseproject.core.routing.flow.decompose.FlowNavigationComponent
import com.kmpbaseproject.core.routing.flow.decompose.FlowTransition
import com.kmpbaseproject.core.routing.flow.decompose.Node
import com.kmpbaseproject.core.routing.flow.decompose.Screen
import com.kmpbaseproject.core.routing.flow.decompose.retainedFlowComponent
import com.kmpbaseproject.core.routing.flow.decompose.viewModel
import com.kmpbaseproject.core.ui.routing.Event
import com.kmpbaseproject.feature.app.routing.AppFlowComponent
import com.kmpbaseproject.feature.app.routing.decompose.AppFlowNavigationComponent.Child
import com.kmpbaseproject.feature.app.routing.decompose.AppFlowNavigationComponent.Config
import com.kmpbaseproject.feature.cities.ui.routing.FlowEvent
import com.kmpbaseproject.feature.cities.ui.screen.cityDetails.CityDetailsViewModel
import com.kmpbaseproject.feature.hometabs.routing.decompose.HomeTabsFlowNavigationComponent
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

  override val childFactory: (Config, ComponentContext) -> Child = { config, componentContext ->
    when (config) {
      is Config.Home -> {
        val homeTabsComponent = componentContext.retainedFlowComponent { component.homeTabsFlowComponent() }
        Child.Home(HomeTabsFlowNavigationComponent(componentContext, homeTabsComponent))
      }
      is Config.CityDetails -> {
        Child.CityDetails(component.viewModel<CityDetailsViewModel>(componentContext, config.cityId))
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
