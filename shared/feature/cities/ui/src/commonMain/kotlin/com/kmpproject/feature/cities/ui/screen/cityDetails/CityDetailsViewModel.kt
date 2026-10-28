package com.kmpproject.feature.cities.ui.screen.cityDetails

import com.kmpproject.core.domain.di.AppFlowScope
import com.kmpproject.core.ui.mvi.BaseViewModel
import com.kmpproject.feature.cities.domain.CitiesModel
import com.kmpproject.feature.cities.ui.routing.FlowEvent
import com.kmpproject.lib.annotation.ViewModel
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import org.orbitmvi.orbit.orbitContainer

@Inject
@ViewModel(AppFlowScope::class)
class CityDetailsViewModel(
  @Assisted
  cityId: Long,
  citiesModel: CitiesModel,
) : BaseViewModel<ViewState, Nothing>() {
  override val container = viewModelScope.orbitContainer<ViewState, Nothing>(ViewState()) {
    citiesModel.cityDetails(cityId)
      .collect { city -> reduce { state.copy(city = city) } }
  }

  fun navigateBack() = intent {
    sendEvent(FlowEvent.CityDetailsDismissed)
  }

  fun searchCityInfo() = intent {
    val city = state.city ?: return@intent
    sendEvent(FlowEvent.CitySearchRequested(city.name))
  }
}
