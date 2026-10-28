package com.kmpproject.feature.map.routing.decompose

import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext
import com.kmpproject.core.routing.flow.decompose.ChildScope
import com.kmpproject.core.routing.flow.decompose.FlowNavigationComponent
import com.kmpproject.core.routing.flow.decompose.FlowTransition
import com.kmpproject.core.routing.flow.decompose.Node
import com.kmpproject.core.routing.flow.decompose.Screen
import com.kmpproject.core.ui.routing.Event
import com.kmpproject.feature.map.routing.MapFlowComponent
import com.kmpproject.feature.map.routing.decompose.MapFlowNavigationComponent.Child
import com.kmpproject.feature.map.routing.decompose.MapFlowNavigationComponent.Config
import com.kmpproject.feature.map.ui.routing.FlowEvent
import com.kmpproject.feature.map.ui.screen.map.screen.map.MapViewModel
import io.ktor.http.URLBuilder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Stable
class MapFlowNavigationComponent(
  context: ComponentContext,
  override val component: MapFlowComponent,
) : FlowNavigationComponent<Config, Child>(context) {
  override val configSerializer: KSerializer<Config> = Config.serializer()

  override fun initialConfig(): List<Config> = listOf(Config.Map)

  override fun transition(event: Event): FlowTransition<Config> {
    return when (event) {
      is FlowEvent.CitySearchRequested -> {
        component.appLauncher().openWebsite(citySearchUrl(event.cityName))
        FlowTransition.Stay
      }
      else -> {
        FlowTransition.Ignore
      }
    }
  }

  override val childFactory: ChildScope.(Config) -> Child = { config ->
    when (config) {
      is Config.Map -> Child.Map(viewModel<MapViewModel>())
    }
  }

  @Serializable
  sealed interface Config {
    @Serializable
    data object Map : Config
  }

  sealed interface Child : Node {
    data class Map(override val viewModel: MapViewModel) : Child, Screen
  }
}

private fun citySearchUrl(cityName: String): String {
  return URLBuilder(SEARCH_URL)
    .apply { parameters.append("q", cityName) }
    .buildString()
}

private const val SEARCH_URL = "https://www.google.com/search"
