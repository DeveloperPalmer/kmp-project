package com.kmpproject.feature.cities.ui.screen.cities

import com.kmpproject.core.ui.mvi.BaseViewModel
import com.kmpproject.feature.cities.domain.CitiesModel
import com.kmpproject.feature.cities.domain.di.CitiesScope
import com.kmpproject.feature.cities.ui.routing.FlowEvent
import com.kmpproject.feature.cities.ui.screen.cities.ViewState.MenuAction
import com.kmpproject.lib.annotation.ViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.orbitmvi.orbit.blockingIntent
import org.orbitmvi.orbit.orbitContainer

@Inject
@ViewModel(CitiesScope::class)
class CitiesViewModel(
  private val citiesModel: CitiesModel,
) : BaseViewModel<ViewState, Nothing>() {
  override val container = viewModelScope.orbitContainer<ViewState, Nothing>(ViewState()) {
    coroutineScope {
      launch { reduce { state.copy(cities = citiesModel.cities) } }
    }
  }

  // Blocking, so the text field gets its value back before the next keystroke
  fun changeQuery(query: String) = blockingIntent {
    reduce { state.copy(citiesSearchQuery = query) }
    citiesModel.search(query)
  }

  fun openDetails(cityId: Long) = intent {
    sendEvent(FlowEvent.CityDetailsRequested(cityId))
  }

  fun onMenuAction(action: MenuAction) = intent {
    when (action) {
      MenuAction.Service -> Unit
      MenuAction.WorkManager -> citiesModel.exportWithWorkManager()
    }
  }
}
