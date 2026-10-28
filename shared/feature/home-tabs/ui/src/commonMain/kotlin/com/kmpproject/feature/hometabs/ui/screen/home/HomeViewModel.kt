package com.kmpproject.feature.hometabs.ui.screen.home

import com.kmpproject.core.ui.mvi.BaseViewModel
import com.kmpproject.feature.hometabs.domain.di.HomeTabsScope
import com.kmpproject.feature.hometabs.ui.entity.Tab
import com.kmpproject.feature.hometabs.ui.routing.FlowEvent
import com.kmpproject.lib.annotation.ViewModel
import me.tatarka.inject.annotations.Inject
import org.orbitmvi.orbit.orbitContainer

@Inject
@ViewModel(HomeTabsScope::class)
class HomeViewModel : BaseViewModel<ViewState, Nothing>() {
  override val container = viewModelScope.orbitContainer<ViewState, Nothing>(ViewState())

  fun navigateBack() {
    selectTab(Tab.Cities)
  }

  fun selectTab(tab: Tab) = intent {
    reduce { state.copy(selectedTab = tab) }
    sendEvent(FlowEvent.TabChangeRequested(tab))
  }
}
