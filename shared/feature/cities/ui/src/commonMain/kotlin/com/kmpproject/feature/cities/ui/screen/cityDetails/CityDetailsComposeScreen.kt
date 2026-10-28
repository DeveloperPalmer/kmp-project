package com.kmpproject.feature.cities.ui.screen.cityDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmpproject.core.ui.formatPopulation
import com.kmpproject.feature.cities.domain.entity.CityDetails
import com.kmpproject.resources.Res
import com.kmpproject.resources.city_details_city
import com.kmpproject.resources.city_details_country
import com.kmpproject.resources.city_details_population
import com.kmpproject.resources.city_details_population_value
import com.kmpproject.resources.city_details_search
import com.kmpproject.resources.city_details_title
import com.kmpproject.uikit.component.button.PrimaryButton
import com.kmpproject.uikit.component.info.InfoItem
import com.kmpproject.uikit.component.topappbar.TopAppBar
import com.kmpproject.uikit.theme.AppTheme
import com.kmpproject.uikit.theme.VSpacer
import com.kmpproject.uikit.theme.WSpacer
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

/**
 * The Compose UI of the screen, shared by the platforms through [CityDetailsScreen]
 */
@Composable
internal fun CityDetailsComposeScreen(viewModel: CityDetailsViewModel) {
  val state by viewModel.collectAsState()
  CityDetailsContent(
    state = state,
    onBack = viewModel::navigateBack,
    onSearchClick = viewModel::searchCityInfo
  )
}

@Composable
private fun CityDetailsContent(
  state: ViewState,
  onBack: () -> Unit,
  onSearchClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(AppTheme.colors.background.primary)
      .safeDrawingPadding()
  ) {
    TopAppBar(
      title = stringResource(Res.string.city_details_title),
      onBack = onBack
    )
    val city = state.city
    if (city != null) {
      CityInfo(city = city)
    }
    WSpacer()
    VSpacer(16.dp)
    PrimaryButton(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      text = stringResource(Res.string.city_details_search),
      onClick = onSearchClick
    )
    VSpacer(16.dp)
  }
}

@Composable
private fun CityInfo(city: CityDetails) {
  Column(modifier = Modifier.padding(horizontal = 16.dp)) {
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
  }
}
