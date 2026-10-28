package com.kmpproject.uikit.component.topappbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kmpproject.resources.Res
import com.kmpproject.resources.ic_arrow_back_24
import com.kmpproject.uikit.theme.AppTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun TopAppBar(
  modifier: Modifier = Modifier,
  title: String? = null,
  onBack: (() -> Unit)? = null,
  actions: @Composable RowScope.() -> Unit = {},
) {
  CenterAlignedTopAppBar(
    modifier = modifier,
    title = {
      if (title != null) {
        Text(
          text = title,
          style = AppTheme.typography.title4,
        )
      }
    },
    navigationIcon = {
      if (onBack != null) {
        IconButton(onClick = onBack) {
          Icon(
            painter = painterResource(Res.drawable.ic_arrow_back_24),
            contentDescription = "arrow back icon",
          )
        }
      }
    },
    actions = actions,
    expandedHeight = 52.dp,
    windowInsets = WindowInsets(0),
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = AppTheme.colors.background.primary,
      titleContentColor = AppTheme.colors.text.primary,
      navigationIconContentColor = AppTheme.colors.icon.primary,
      actionIconContentColor = AppTheme.colors.icon.primary,
    ),
  )
}

@Preview
@Composable
private fun TopAppBarPreviewLight() {
  AppTheme(useDarkTheme = false) {
    TopAppBarPreviewContent()
  }
}

@Preview
@Composable
private fun TopAppBarPreviewDark() {
  AppTheme(useDarkTheme = true) {
    TopAppBarPreviewContent()
  }
}

@Composable
private fun TopAppBarPreviewContent() {
  TopAppBar(
    title = "Города",
    onBack = {},
  )
}
