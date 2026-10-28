@file:Suppress("UnusedImport")

package com.kmpproject.feature.cities.data

import androidx.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneNotNull
import app.cash.sqldelight.paging3.QueryPagingSource
import com.kmpproject.core.data.cities.CitiesDatabase
import com.kmpproject.feature.cities.data.entity.CitiesRequest
import com.kmpproject.feature.cities.data.entity.CitiesResponse
import com.kmpproject.feature.cities.domain.CitiesRepository
import com.kmpproject.feature.cities.domain.entity.City
import com.kmpproject.feature.cities.domain.entity.CityDetails
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class CitiesDataRepository(
  private val httpClient: HttpClient,
  private val citiesDatabase: CitiesDatabase,
) : CitiesRepository {
  override suspend fun fetchCities(query: String, page: Int, limit: Int) {
    return withContext(Dispatchers.IO) {
      val request = CitiesRequest(
        query = query,
        page = page,
        limit = limit
      )
      val response = httpClient.get(request).body<CitiesResponse>()

      citiesDatabase.transaction {
        if (page == FIRST_PAGE) {
          citiesDatabase.citySearchResultQueries.deleteBySearchQuery(query)
        }
        response.items.forEachIndexed { index, city ->
          citiesDatabase.cityQueries.insert(
            id = city.id,
            name = city.name,
            country = city.country,
            lat = city.lat,
            lon = city.lon,
            pop = city.pop,
          )
          citiesDatabase.citySearchResultQueries.insert(
            cityId = city.id,
            searchQuery = query,
            position = positionInSearch(
              page = page,
              pageSize = limit,
              indexOnPage = index
            ),
          )
        }
        citiesDatabase.citySearchQueries.upsert(
          searchQuery = query,
          nextPage = nextPageOrNull(
            page = page,
            pageSize = limit,
            total = response.total
          ),
        )
      }
    }
  }

  override suspend fun nextPage(query: String): Int? {
    return withContext(Dispatchers.IO) {
      citiesDatabase.citySearchQueries
        .getNextPage(query)
        .executeAsOneOrNull()
        ?.nextPage
        ?.toInt()
    }
  }

  override suspend fun cities(query: String): List<City> {
    return withContext(Dispatchers.IO) {
      citiesDatabase.citySearchResultQueries
        .getAllCities(
          searchQuery = query,
          mapper = ::City
        )
        .executeAsList()
    }
  }

  override fun citiesPagingSource(query: String): PagingSource<Int, City> {
    return QueryPagingSource(
      context = Dispatchers.IO,
      transacter = citiesDatabase.citySearchResultQueries,
      countQuery = citiesDatabase.citySearchResultQueries.countQuery(query),
      queryProvider = { limit, offset ->
        citiesDatabase.citySearchResultQueries.getCities(
          searchQuery = query,
          limit = limit,
          offset = offset,
          mapper = ::City
        )
      }
    )
  }

  override fun cityDetails(id: Long): Flow<CityDetails> {
    return citiesDatabase.cityQueries
      .getCity(
        id = id,
        mapper = ::CityDetails
      )
      .asFlow()
      .mapToOneNotNull(Dispatchers.IO)
  }
}

private fun positionInSearch(page: Int, pageSize: Int, indexOnPage: Int): Long {
  return ((page - FIRST_PAGE) * pageSize + indexOnPage).toLong()
}

private fun nextPageOrNull(page: Int, pageSize: Int, total: Int): Long? {
  val loadedCount = page * pageSize
  return if (loadedCount < total) (page + 1).toLong() else null
}

private const val FIRST_PAGE = 1
