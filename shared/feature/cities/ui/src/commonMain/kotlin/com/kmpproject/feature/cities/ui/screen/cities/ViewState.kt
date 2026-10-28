package com.kmpproject.feature.cities.ui.screen.cities

import androidx.compose.runtime.Immutable
import androidx.paging.PagingData
import com.kmpproject.feature.cities.domain.entity.City
import com.kmpproject.resources.Res
import com.kmpproject.resources.cities_download_with_service
import com.kmpproject.resources.cities_download_with_work_manager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.StringResource
import kotlin.enums.EnumEntries

@Immutable
data class ViewState(
  val cities: Flow<PagingData<City>> = emptyFlow(),
  val citiesSearchQuery: String = "",
  val menuActions: EnumEntries<MenuAction> = MenuAction.entries
) {
  enum class MenuAction(val title: StringResource) {
    Service(Res.string.cities_download_with_service),
    WorkManager(Res.string.cities_download_with_work_manager)
  }
}
