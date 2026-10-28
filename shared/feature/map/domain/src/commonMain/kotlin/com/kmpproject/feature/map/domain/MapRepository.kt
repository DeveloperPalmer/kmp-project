package com.kmpproject.feature.map.domain

import com.kmpproject.feature.map.domain.entity.MapCity
import com.kmpproject.feature.map.domain.entity.MapCityDetails
import com.kmpproject.feature.map.domain.entity.MapViewport
import kotlinx.coroutines.flow.Flow

interface MapRepository {
  suspend fun cities(viewport: MapViewport): List<MapCity>
  fun cityDetails(id: Long): Flow<MapCityDetails>
}
