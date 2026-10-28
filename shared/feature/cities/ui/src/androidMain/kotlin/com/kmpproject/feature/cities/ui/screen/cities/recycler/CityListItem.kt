package com.kmpproject.feature.cities.ui.screen.cities.recycler

import androidx.annotation.StringRes
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.kmpproject.feature.cities.domain.entity.City
import com.kmpproject.resources.R

/**
 * Строка списка городов. В одном RecyclerView строки двух видов, и у каждого вида своя разметка и свой ViewHolder:
 * адаптер различает их по viewType
 */
internal sealed interface CityListItem {
  // Город из Paging
  data class CityRow(val city: City) : CityListItem

  // Заголовок группы: в данных его нет, его вставляет withGroupHeaders
  data class GroupHeader(val group: PopulationGroup) : CityListItem
}

/**
 * Группа городов по населению. Список отсортирован по населению по убыванию, поэтому каждая группа идёт одним
 * куском, и заголовок нужен только там, где группа сменилась
 */
internal enum class PopulationGroup(
  private val minPopulation: Long,
  @param:StringRes val title: Int,
) {
  Over20M(
    minPopulation = 20_000_000,
    title = R.string.cities_group_over_20m
  ),
  From10To20M(
    minPopulation = 10_000_000,
    title = R.string.cities_group_10_20m
  ),
  From5To10M(
    minPopulation = 5_000_000,
    title = R.string.cities_group_5_10m
  ),
  From1To5M(
    minPopulation = 1_000_000,
    title = R.string.cities_group_1_5m
  ),
  Under1M(
    minPopulation = 0,
    title = R.string.cities_group_under_1m
  );

  companion object {
    // Группы перечислены от больших к меньшим, поэтому первая подходящая и есть нужная
    fun of(population: Long): PopulationGroup = entries.first { population >= it.minPopulation }
  }
}

/**
 * Города вперемешку с заголовками групп. insertSeparators вызывает лямбду для каждой пары соседей, в том числе
 * перед первым городом (before = null) и после последнего (after = null): вернуть строку — вставить её между ними,
 * null — ничего не вставлять.
 *
 * Обычно такие преобразования делают во ViewModel до cachedIn, чтобы они не повторялись при каждом сборе. Здесь они
 * в Android-коде: заголовки нужны только этому списку, а ViewModel и Compose-версия общие с iOS
 */
internal fun PagingData<City>.withGroupHeaders(): PagingData<CityListItem> {
  return map<City, CityListItem> { city -> CityListItem.CityRow(city) }
    .insertSeparators { before, after ->
      // После последнего города заголовок не нужен
      val next = (after as? CityListItem.CityRow)?.city ?: return@insertSeparators null
      val previous = (before as? CityListItem.CityRow)?.city
      val group = PopulationGroup.of(next.population)
      // Заголовок — перед первым городом и там, где группа сменилась
      if (previous == null || PopulationGroup.of(previous.population) != group) {
        CityListItem.GroupHeader(group)
      } else {
        null
      }
    }
}
