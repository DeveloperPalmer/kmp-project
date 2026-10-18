package com.kmpbaseproject.feature.hometabs.routing.decompose

import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.navigate
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kmpbaseproject.core.routing.flow.decompose.ChildScope
import com.kmpbaseproject.core.routing.flow.decompose.Flow
import com.kmpbaseproject.core.routing.flow.decompose.FlowNode
import com.kmpbaseproject.core.routing.flow.decompose.FlowTransition
import com.kmpbaseproject.core.ui.routing.Event
import com.kmpbaseproject.feature.cities.routing.decompose.CitiesFlowNavigationComponent
import com.kmpbaseproject.feature.hometabs.routing.HomeTabsFlowComponent
import com.kmpbaseproject.feature.hometabs.routing.decompose.HomeTabsFlowNavigationComponent.Config
import com.kmpbaseproject.feature.hometabs.ui.entity.Tab
import com.kmpbaseproject.feature.hometabs.ui.routing.FlowEvent
import com.kmpbaseproject.feature.hometabs.ui.screen.home.HomeViewModel
import com.kmpbaseproject.feature.map.routing.decompose.MapFlowNavigationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Stable
class HomeTabsFlowNavigationComponent(
  componentContext: ComponentContext,
  val component: HomeTabsFlowComponent,
) : FlowNode<Config>(), BackHandlerOwner by componentContext {
  private val eventScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
  private val pagesNavigation = PagesNavigation<Config>()

  val viewModel = ChildScope(componentContext, component).viewModel<HomeViewModel>()

  val pages: Value<ChildPages<Config, Child>> = componentContext.childPages(
    source = pagesNavigation,
    serializer = Config.serializer(),
    childFactory = { config, childContext -> ChildScope(childContext, component).child(config) },
    initialPages = {
      Pages(
        selectedIndex = 0,
        items = listOf(Config.Cities, Config.Map)
      )
    },
  )

  init {
    eventScope.launch {
      viewModel.events.collect { event -> dispatch(event) }
    }
    componentContext.lifecycle.doOnDestroy {
      eventScope.cancel()
    }
  }

  fun selectPage(index: Int) {
    return pagesNavigation.select(index = index)
  }

  override fun transition(event: Event): FlowTransition<Config> {
    return when (event) {
      is FlowEvent.TabChangeRequested -> {
        FlowTransition.NavigateTo(event.tab.config())
      }
      else -> {
        FlowTransition.Ignore
      }
    }
  }

  override fun navigateTo(config: Config) {
    pagesNavigation.navigate { current -> current.copy(selectedIndex = current.items.indexOf(config)) }
  }

  // Tabs have no back stack
  override fun navigateBack() = Unit

  private fun ChildScope.child(config: Config): Child {
    val child = when (config) {
      is Config.Cities -> {
        val component = CitiesFlowNavigationComponent(
          context = context,
          component = flowComponent { component.citiesFlowComponent() }
        )
        Child.Cities(component)
      }
      is Config.Map -> {
        val component = MapFlowNavigationComponent(
          context = context,
          component = flowComponent { component.mapFlowComponent() }
        )
        Child.Map(component)
      }
    }
    adopt(child)
    return child
  }

  private fun Tab.config(): Config {
    return when (this) {
      Tab.Cities -> Config.Cities
      Tab.Map -> Config.Map
    }
  }

  @Serializable
  sealed interface Config {
    @Serializable
    data object Cities : Config

    @Serializable
    data object Map : Config
  }

  sealed interface Child : Flow {
    data class Cities(override val component: CitiesFlowNavigationComponent) : Child
    data class Map(override val component: MapFlowNavigationComponent) : Child
  }
}
