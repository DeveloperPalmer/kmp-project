package com.kmpproject.feature.cities.domain

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kmpproject.core.domain.ReactiveModel
import com.kmpproject.core.domain.di.AppFlowScope
import com.kmpproject.feature.cities.domain.entity.City
import com.kmpproject.feature.cities.domain.entity.CityDetails
import com.kmpproject.feature.cities.domain.export.CitiesExportScheduler
import com.kmpproject.feature.cities.domain.mediator.CitiesRemoteMediator
import com.kmpproject.feature.cities.domain.mediator.PAGE_SIZE
import com.kmpproject.lib.annotation.FlowCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Inject
@SingleIn(AppFlowScope::class)
class CitiesModel(
  @FlowCoroutineScope(AppFlowScope::class)
  coroutineScope: CoroutineScope,
  private val citiesRepository: CitiesRepository,
  private val citiesExportScheduler: CitiesExportScheduler,
) : ReactiveModel(coroutineScope) {
  private val stateFlow = MutableStateFlow(State())

  val cities: Flow<PagingData<City>> = stateFlow
    .map { it.citiesSearchQuery }
    // An empty query (first load, cleared search) goes through at once
    .debounce { query -> if (query.isEmpty()) Duration.ZERO else SEARCH_DEBOUNCE }
    .distinctUntilChanged()
    .flatMapLatest { query ->
      Pager(
        config = PagingConfig(
          pageSize = PAGE_SIZE,
          enablePlaceholders = false
        ),
        remoteMediator = CitiesRemoteMediator(
          query = query,
          citiesRepository = citiesRepository
        ),
        pagingSourceFactory = { citiesRepository.citiesPagingSource(query) },
      ).flow
    }
    .cachedIn(scope)

  fun cityDetails(id: Long): Flow<CityDetails> = citiesRepository.cityDetails(id)

  fun search(query: String) {
    stateFlow.update { it.copy(citiesSearchQuery = query) }
  }

  fun exportWithWorkManager() {
    citiesExportScheduler.scheduleOneTime(stateFlow.value.citiesSearchQuery)
  }

  private data class State(
    val citiesSearchQuery: String = ""
  )
}

private val SEARCH_DEBOUNCE = 300.milliseconds
