package com.kmpproject.feature.map.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.kmpproject.resources.Res
import com.kmpproject.resources.ic_minus_24
import com.kmpproject.resources.ic_plus_24
import com.kmpproject.resources.map_zoom_in
import com.kmpproject.resources.map_zoom_out
import com.kmpproject.uikit.modifier.surface
import com.kmpproject.uikit.theme.AppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ZoomControl(
  visible: Boolean,
  onZoomIn: () -> Unit,
  onZoomOut: () -> Unit,
  modifier: Modifier = Modifier,
) {
  AnimatedVisibility(
    modifier = modifier,
    visible = visible,
    enter = EnterTransition.None,
    exit = ExitTransition.None,
  ) {
    val alpha = transition.animateFloat(
      label = "alpha",
      targetValueByState = { if (it == EnterExitState.Visible) 1f else 0f }
    )
    Column(
      modifier = Modifier
        .width(52.dp)
        .graphicsLayer {
          this.alpha = alpha.value
          compositingStrategy = CompositingStrategy.ModulateAlpha
        }
        .surface(
          shape = AppTheme.shapes.semiMedium,
          shadows = AppTheme.shadows.label,
          backgroundColor = AppTheme.colors.background.primary
        )
    ) {
      ZoomButton(
        icon = Res.drawable.ic_plus_24,
        contentDescription = stringResource(Res.string.map_zoom_in),
        onClick = onZoomIn,
      )
      HorizontalDivider(
        thickness = 2.dp,
        color = AppTheme.colors.divider.primary,
      )
      ZoomButton(
        icon = Res.drawable.ic_minus_24,
        contentDescription = stringResource(Res.string.map_zoom_out),
        onClick = onZoomOut,
      )
    }
  }
}

@Composable
private fun ZoomButton(
  icon: DrawableResource,
  contentDescription: String,
  onClick: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(46.dp)
      .clickable(
        role = Role.Button,
        indication = null,
        interactionSource = null,
        onClick = onClick,
      ),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      painter = painterResource(icon),
      contentDescription = contentDescription,
      tint = AppTheme.colors.icon.primary,
    )
  }
}
