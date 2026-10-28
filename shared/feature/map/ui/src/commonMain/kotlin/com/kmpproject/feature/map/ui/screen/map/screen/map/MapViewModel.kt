package com.kmpproject.feature.map.ui.screen.map.screen.map

import com.kmpproject.core.ui.mvi.BaseViewModel
import com.kmpproject.feature.map.domain.MapModel
import com.kmpproject.feature.map.domain.di.MapScope
import com.kmpproject.feature.map.domain.entity.MapViewport
import com.kmpproject.feature.map.ui.mapper.toCityInfo
import com.kmpproject.feature.map.ui.mapper.toMapPins
import com.kmpproject.feature.map.ui.routing.FlowEvent
import com.kmpproject.lib.annotation.ViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.orbitmvi.orbit.orbitContainer

@Inject
@ViewModel(MapScope::class)
class MapViewModel(
  private val mapModel: MapModel,
) : BaseViewModel<ViewState, SideEffect>() {
  override val container = viewModelScope.orbitContainer<ViewState, SideEffect>(ViewState()) {
    coroutineScope {
      launch {
        mapModel.cities.collect { cities ->
          reduce { state.copy(pins = cities.toMapPins(mapped = state.pins)) }
        }
      }
      launch {
        mapModel.selectedCity.collect { city ->
          reduce { state.copy(city = city?.toCityInfo()) }
        }
      }
    }
  }

  fun zoomIn() = intent {
    postSideEffect(SideEffect.MapAction.ZoomIn)
  }

  fun zoomOut() = intent {
    postSideEffect(SideEffect.MapAction.ZoomOut)
  }

  fun changeViewport(viewport: MapViewport) {
    mapModel.changeViewport(viewport)
  }

  fun selectCity(cityId: Long) {
    mapModel.selectCity(cityId)
  }

  fun dismissCity() {
    mapModel.deselectCity()
  }

  fun searchCityInfo() = intent {
    val city = state.city ?: return@intent
    sendEvent(FlowEvent.CitySearchRequested(city.name))
  }
}
