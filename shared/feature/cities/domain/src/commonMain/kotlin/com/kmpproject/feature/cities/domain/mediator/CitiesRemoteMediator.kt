package com.kmpproject.feature.cities.domain.mediator

import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.kmpproject.core.domain.entity.ApiError
import com.kmpproject.core.domain.entity.ConnectivityError
import com.kmpproject.feature.cities.domain.CitiesRepository
import com.kmpproject.feature.cities.domain.entity.City

internal class CitiesRemoteMediator(
  private val query: String,
  private val citiesRepository: CitiesRepository,
) : RemoteMediator<Int, City>() {
  override suspend fun load(loadType: LoadType, state: PagingState<Int, City>): MediatorResult {
    val page = when (loadType) {
      LoadType.REFRESH -> {
        FIRST_PAGE
      }
      LoadType.PREPEND -> {
        return MediatorResult.Success(endOfPaginationReached = true)
      }
      LoadType.APPEND -> {
        citiesRepository.nextPage(query) ?: return MediatorResult.Success(endOfPaginationReached = true)
      }
    }
    return try {
      citiesRepository.fetchCities(
        query = query,
        page = page,
        limit = PAGE_SIZE
      )
      MediatorResult.Success(
        endOfPaginationReached = citiesRepository.nextPage(query) == null
      )
    } catch (e: ConnectivityError) {
      MediatorResult.Error(e)
    } catch (e: ApiError) {
      MediatorResult.Error(e)
    }
  }
}

internal const val PAGE_SIZE = 20
private const val FIRST_PAGE = 1
