package com.kmpproject.uikit.component.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kmpproject.core.ui.safeDrawingHorizontal
import com.kmpproject.uikit.modifier.surface
import com.kmpproject.uikit.theme.AppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomBar(
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit,
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .surface(
        backgroundColor = AppTheme.colors.background.primary,
        shape = RectangleShape,
        shadows = AppTheme.shadows.bottomBar,
      )
      .windowInsetsPadding(WindowInsets.safeDrawingHorizontal)
      .navigationBarsPadding()
      .height(64.dp)
      .padding(horizontal = 8.dp)
      .selectableGroup(),
    verticalAlignment = Alignment.CenterVertically,
    content = content,
  )
}

@Composable
fun RowScope.BottomBarItem(
  selected: Boolean,
  icon: DrawableResource,
  contentDescription: String?,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .weight(1f)
      .fillMaxHeight()
      .selectable(
        role = Role.Tab,
        selected = selected,
        indication = null,
        interactionSource = null,
        onClick = onClick,
      ),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      modifier = Modifier
        .background(
          shape = AppTheme.shapes.circle,
          color = if (selected) AppTheme.colors.background.brand else Color.Transparent,
        )
        .padding(
          vertical = 4.dp,
          horizontal = 20.dp
        ),
      painter = painterResource(icon),
      contentDescription = contentDescription,
      tint = if (selected) AppTheme.colors.icon.primary else AppTheme.colors.icon.secondary,
    )
  }
}
