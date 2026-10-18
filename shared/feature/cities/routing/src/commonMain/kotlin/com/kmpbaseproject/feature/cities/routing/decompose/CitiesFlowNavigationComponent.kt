package com.kmpbaseproject.feature.cities.routing.decompose

import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext
import com.kmpbaseproject.core.routing.flow.decompose.FlowNavigationComponent
import com.kmpbaseproject.core.routing.flow.decompose.FlowTransition
import com.kmpbaseproject.core.routing.flow.decompose.Node
import com.kmpbaseproject.core.routing.flow.decompose.Screen
import com.kmpbaseproject.core.routing.flow.decompose.viewModel
import com.kmpbaseproject.core.ui.routing.Event
import com.kmpbaseproject.feature.cities.routing.CitiesFlowComponent
import com.kmpbaseproject.feature.cities.routing.decompose.CitiesFlowNavigationComponent.Child
import com.kmpbaseproject.feature.cities.routing.decompose.CitiesFlowNavigationComponent.Config
import com.kmpbaseproject.feature.cities.ui.screen.cities.CitiesViewModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Stable
class CitiesFlowNavigationComponent(
  context: ComponentContext,
  override val component: CitiesFlowComponent,
) : FlowNavigationComponent<Config, Child>(context) {
  override val configSerializer: KSerializer<Config> = Config.serializer()

  override fun initialConfig(): List<Config> = listOf(Config.Cities)

  override fun transition(event: Event): FlowTransition<Config> {
    return FlowTransition.Ignore
  }

  override val childFactory: (Config, ComponentContext) -> Child = { config, componentContext ->
    when (config) {
      is Config.Cities -> Child.Cities(component.viewModel<CitiesViewModel>(componentContext))
    }
  }

  @Serializable
  sealed interface Config {
    @Serializable
    data object Cities : Config
  }

  sealed interface Child : Node {
    data class Cities(override val viewModel: CitiesViewModel) : Child, Screen
  }
}
