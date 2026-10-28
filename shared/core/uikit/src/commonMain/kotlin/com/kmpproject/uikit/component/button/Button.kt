package com.kmpproject.uikit.component.button

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kmpproject.uikit.modifier.surface
import com.kmpproject.uikit.theme.AppTheme

@Composable
internal fun ButtonInternal(
  text: String,
  colors: ButtonColors,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
  val containerColor = colors.containerColor(
    pressed = interactionSource.collectIsPressedAsState().value,
  )
  Box(
    modifier = modifier
      .heightIn(56.dp)
      .surface(
        role = Role.Button,
        shape = AppTheme.shapes.regular,
        backgroundColor = { containerColor.value },
        interactionSource = interactionSource,
        onClick = onClick,
      ),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      modifier = Modifier.padding(horizontal = 24.dp),
      text = text,
      color = colors.contentColor,
      style = AppTheme.typography.title4,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Immutable
internal data class ButtonColors(
  val contentColor: Color,
  val containerColor: Color,
  val pressedContainerColor: Color,
)

@Immutable
internal object ButtonDefaults {
  @Composable
  fun primaryDefaultColors(): ButtonColors {
    return ButtonColors(
      containerColor = AppTheme.colors.button.primaryBackground,
      pressedContainerColor = AppTheme.colors.button.primaryBackgroundPressed,
      contentColor = AppTheme.colors.button.primaryForeground,
    )
  }
}

@Composable
private fun ButtonColors.containerColor(pressed: Boolean): State<Color> {
  return rememberUpdatedState(if (pressed) pressedContainerColor else containerColor)
}
