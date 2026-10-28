package com.kmpproject.feature.cities.ui.screen.cities

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kmpproject.core.ui.entity.ContentLoadState
import com.kmpproject.core.ui.entity.UiError
import com.kmpproject.core.ui.toUiLceState
import com.kmpproject.feature.cities.domain.entity.City
import com.kmpproject.feature.cities.ui.screen.cities.ViewState.MenuAction
import com.kmpproject.resources.Res
import com.kmpproject.resources.cities_list_title
import com.kmpproject.resources.cities_search_placeholder
import com.kmpproject.resources.ic_more_vert_24
import com.kmpproject.resources.ic_pin_24
import com.kmpproject.resources.ic_search_24
import com.kmpproject.resources.retry
import com.kmpproject.uikit.component.button.PrimaryButton
import com.kmpproject.uikit.component.message.ErrorMessage
import com.kmpproject.uikit.component.shimmer.ShimmerSpacer
import com.kmpproject.uikit.component.textfield.PrimaryTextField
import com.kmpproject.uikit.component.topappbar.TopAppBar
import com.kmpproject.uikit.theme.AppTheme
import com.kmpproject.uikit.theme.VSpacer
import com.kmpproject.uikit.theme.WSpacer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.orbitmvi.orbit.compose.collectAsState

/**
 * The Compose UI of the screen, shared by the platforms through [CitiesScreen]
 */
@Composable
internal fun CitiesComposeScreen(viewModel: CitiesViewModel) {
  val state by viewModel.collectAsState()
  val cities = state.cities.collectAsLazyPagingItems()
  when (val contentLoadState = cities.loadState.refresh.toUiLceState()) {
    is ContentLoadState.Error -> {
      CitiesError(
        error = contentLoadState.error,
        onRetry = cities::retry
      )
    }
    is ContentLoadState.Loading,
    is ContentLoadState.Ready -> {
      CitiesReady(
        query = state.citiesSearchQuery,
        cities = cities,
        isLoading = contentLoadState == ContentLoadState.Loading,
        menuActions = state.menuActions,
        onQueryChange = viewModel::changeQuery,
        onCityClick = viewModel::openDetails,
        onMenuAction = viewModel::onMenuAction,
      )
    }
  }
}

@Composable
private fun CitiesReady(
  query: String,
  cities: LazyPagingItems<City>,
  isLoading: Boolean,
  menuActions: List<MenuAction>,
  onQueryChange: (String) -> Unit,
  onCityClick: (Long) -> Unit,
  onMenuAction: (MenuAction) -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(AppTheme.colors.background.primary)
      .safeDrawingPadding()
  ) {
    TopAppBar(
      title = stringResource(Res.string.cities_list_title),
      actions = {
        OverflowMenu(
          items = menuActions,
          onItemClick = onMenuAction
        )
      }
    )
    VSpacer(8.dp)
    PrimaryTextField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      value = query,
      onValueChange = onQueryChange,
      placeholder = stringResource(Res.string.cities_search_placeholder),
      trailingIcon = Res.drawable.ic_search_24,
      trailingIconDescription = "search icon"
    )
    VSpacer(8.dp)
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
      if (isLoading) {
        itemsIndexed(SKELETON_WIDTHS) { index, width ->
          Skeleton(
            width = width,
            showDivider = index > 0
          )
        }
      } else {
        items(
          key = cities.itemKey { it.id },
          count = cities.itemCount
        ) { index ->
          val city = cities[index]
          if (city != null) {
            City(
              modifier = Modifier.clickable(
                indication = null,
                interactionSource = null,
                onClick = { onCityClick(city.id) }
              ),
              showDivider = index > 0
            ) {
              Text(
                modifier = Modifier.padding(vertical = 4.dp),
                text = city.title,
                style = AppTheme.typography.subtitle1,
                color = AppTheme.colors.text.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
        if (cities.loadState.append is LoadState.Loading) {
          item {
            Skeleton(
              width = SKELETON_WIDTHS.first(),
              showDivider = cities.itemCount > 0
            )
          }
        }
      }
    }
  }
}

@Composable
private fun Skeleton(
  width: Dp,
  showDivider: Boolean,
) {
  City(showDivider = showDivider) {
    ShimmerSpacer(
      modifier = Modifier.padding(vertical = 4.dp),
      width = width,
      height = 22.dp
    )
  }
}

@Composable
private fun City(
  showDivider: Boolean,
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit,
) {
  Column(modifier = modifier) {
    if (showDivider) {
      HorizontalDivider(
        thickness = 1.dp,
        color = AppTheme.colors.divider.primary
      )
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Icon(
        painter = painterResource(Res.drawable.ic_pin_24),
        tint = AppTheme.colors.icon.secondary,
        contentDescription = "pin icon"
      )
      content()
    }
  }
}

@Composable
private fun CitiesError(
  error: UiError,
  onRetry: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(AppTheme.colors.background.primary)
      .safeDrawingPadding()
      .padding(horizontal = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    WSpacer()
    ErrorMessage(
      message = error.message
    )
    WSpacer()
    VSpacer(16.dp)
    PrimaryButton(
      modifier = Modifier.fillMaxWidth(),
      text = stringResource(Res.string.retry),
      onClick = onRetry
    )
    VSpacer(16.dp)
  }
}

@Composable
private fun OverflowMenu(
  items: List<MenuAction>,
  onItemClick: (MenuAction) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
  Box {
    IconButton(onClick = { expanded = true }) {
      Icon(
        painter = painterResource(Res.drawable.ic_more_vert_24),
        contentDescription = "more icon",
      )
    }
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      shape = AppTheme.shapes.small,
      containerColor = AppTheme.colors.background.primary,
    ) {
      items.forEach { item ->
        DropdownMenuItem(
          text = {
            Text(
              text = stringResource(item.title),
              style = AppTheme.typography.body1,
            )
          },
          onClick = {
            expanded = false
            onItemClick(item)
          },
          colors = MenuDefaults.itemColors(
            textColor = AppTheme.colors.text.primary,
          ),
        )
      }
    }
  }
}

private val SKELETON_WIDTHS = listOf(144.dp, 180.dp, 144.dp, 180.dp, 144.dp)
