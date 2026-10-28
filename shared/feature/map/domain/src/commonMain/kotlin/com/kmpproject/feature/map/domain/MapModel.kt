package com.kmpproject.feature.map.domain

import com.kmpproject.core.domain.ReactiveModel
import com.kmpproject.core.domain.mapDistinctChanges
import com.kmpproject.core.domain.mapDistinctNotNullChanges
import com.kmpproject.feature.map.domain.di.MapScope
import com.kmpproject.feature.map.domain.entity.MapCity
import com.kmpproject.feature.map.domain.entity.MapCityDetails
import com.kmpproject.feature.map.domain.entity.MapViewport
import com.kmpproject.feature.map.domain.mapper.settledAreas
import com.kmpproject.lib.annotation.FlowCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Inject
@SingleIn(MapScope::class)
class MapModel(
  @FlowCoroutineScope(MapScope::class)
  coroutineScope: CoroutineScope,
  mapRepository: MapRepository,
) : ReactiveModel(coroutineScope) {
  private val stateFlow = MutableStateFlow(State())

  /**
   * Every city loaded so far: each value only adds cities to the end of the previous one, and a
   * response with no new cities emits nothing, so the map is not rebuilt each time the camera stops.
   */
  val cities: StateFlow<List<MapCity>> = stateFlow
    .mapDistinctNotNullChanges { it.viewport }
    .settledAreas()
    .flatMapLatest { viewport ->
      flow { emit(mapRepository.cities(viewport)) }
        // The last cities stay on the map; a failed area is requested again once the camera moves away.
        .catch { error -> error.printStackTrace() }
    }
    .mapNotNull { response -> stateFlow.value.loadedCities.add(response) }
    .stateIn(
      scope = scope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val selectedCity: Flow<MapCityDetails?> = stateFlow
    .mapDistinctChanges { it.selectedCityId }
    .flatMapLatest { id -> if (id == null) flowOf(null) else mapRepository.cityDetails(id) }

  fun changeViewport(viewport: MapViewport) {
    stateFlow.update { it.copy(viewport = viewport) }
  }

  fun selectCity(id: Long) {
    stateFlow.update { it.copy(selectedCityId = id) }
  }

  fun deselectCity() {
    stateFlow.update { it.copy(selectedCityId = null) }
  }
}

private data class State(
  val viewport: MapViewport? = null,
  val selectedCityId: Long? = null,
  val loadedCities: LoadedCities = LoadedCities()
)
