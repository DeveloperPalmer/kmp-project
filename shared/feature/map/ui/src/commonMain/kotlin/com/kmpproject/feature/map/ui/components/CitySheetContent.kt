package com.kmpproject.feature.map.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmpproject.core.ui.formatPopulation
import com.kmpproject.feature.map.ui.entity.CityInfo
import com.kmpproject.resources.Res
import com.kmpproject.resources.city_details_city
import com.kmpproject.resources.city_details_country
import com.kmpproject.resources.city_details_population
import com.kmpproject.resources.city_details_population_value
import com.kmpproject.resources.city_details_search
import com.kmpproject.uikit.component.button.PrimaryButton
import com.kmpproject.uikit.component.info.InfoItem
import com.kmpproject.uikit.theme.VSpacer
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CitySheetContent(
  city: CityInfo,
  onSearchClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.padding(horizontal = 16.dp),
  ) {
    VSpacer(8.dp)
    InfoItem(
      label = stringResource(Res.string.city_details_city),
      value = city.name
    )
    InfoItem(
      label = stringResource(Res.string.city_details_country),
      value = city.country
    )
    InfoItem(
      label = stringResource(Res.string.city_details_population),
      value = stringResource(Res.string.city_details_population_value, formatPopulation(city.population))
    )
    VSpacer(16.dp)
    PrimaryButton(
      modifier = Modifier.fillMaxWidth(),
      text = stringResource(Res.string.city_details_search),
      onClick = onSearchClick
    )
    VSpacer(16.dp)
  }
}
