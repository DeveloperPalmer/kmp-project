package com.kmpproject.uikit.component.textfield

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.kmpproject.uikit.modifier.surface
import com.kmpproject.uikit.theme.AppTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.material3.TextFieldDefaults as TextFieldDefaultsInternal

@Composable
internal fun TextFieldInternal(
  value: String,
  onValueChange: (String) -> Unit,
  colors: TextFieldColors,
  shape: Shape,
  placeholder: String,
  trailingIcon: DrawableResource,
  trailingIconDescription: String,
  modifier: Modifier = Modifier,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
  val focused = interactionSource.collectIsFocusedAsState().value
  val textColor = colors.textColor(
    focused = focused,
  )
  val containerColor = colors.containerColor(
    focused = focused,
  )
  val indicatorColor = colors.indicatorColor(
    focused = focused,
  )
  val placeholderColor = colors.placeholderColor(
    focused = focused,
  )
  val trailingIconColor = colors.trailingIconColor(
    focused = focused,
  )
  BasicTextField(
    modifier = modifier.clearFocusOnKeyboardDismiss(),
    value = value,
    onValueChange = onValueChange,
    singleLine = true,
    keyboardOptions = keyboardOptions,
    textStyle = AppTheme.typography.subtitle1.copy(
      color = textColor.value,
    ),
    cursorBrush = SolidColor(
      value = colors.cursorColor,
    ),
    interactionSource = interactionSource,
    decorationBox = { innerTextField ->
      TextFieldDecoration(
        value = value,
        shape = shape,
        shadows = if (focused) AppTheme.shadows.focus else emptyList(),
        containerColor = containerColor.value,
        indicatorColor = indicatorColor.value,
        placeholderColor = placeholderColor.value,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        trailingIconDescription = trailingIconDescription,
        trailingIconColor = trailingIconColor.value,
        innerTextField = innerTextField,
      )
    },
  )
}

@Composable
private fun TextFieldDecoration(
  value: String,
  shape: Shape,
  shadows: List<Shadow>,
  containerColor: Color,
  indicatorColor: Color,
  placeholderColor: Color,
  placeholder: String,
  trailingIcon: DrawableResource,
  trailingIconDescription: String,
  trailingIconColor: Color,
  modifier: Modifier = Modifier,
  innerTextField: @Composable () -> Unit,
) {
  Row(
    modifier = modifier
      .surface(
        shape = shape,
        shadows = shadows,
        backgroundColor = containerColor,
        border = BorderStroke(1.5.dp, indicatorColor)
      )
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Box(
      modifier = Modifier.weight(1f),
      contentAlignment = Alignment.CenterStart,
    ) {
      if (value.isEmpty()) {
        Text(
          text = placeholder,
          style = AppTheme.typography.subtitle1,
          color = placeholderColor,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
      innerTextField()
    }
    Icon(
      painter = painterResource(trailingIcon),
      tint = trailingIconColor,
      contentDescription = trailingIconDescription,
    )
  }
}

@Immutable
internal object TextFieldDefaults {
  @Composable
  fun primaryDefaultColors(): TextFieldColors {
    return TextFieldDefaultsInternal.colors(
      focusedTextColor = AppTheme.colors.text.primary,
      unfocusedTextColor = AppTheme.colors.text.primary,
      focusedContainerColor = AppTheme.colors.background.primary,
      unfocusedContainerColor = AppTheme.colors.background.tertiary,
      cursorColor = AppTheme.colors.accent.brand,
      focusedIndicatorColor = AppTheme.colors.accent.brand,
      unfocusedIndicatorColor = Color.Transparent,
      focusedPlaceholderColor = AppTheme.colors.text.tertiary,
      unfocusedPlaceholderColor = AppTheme.colors.text.tertiary,
      focusedTrailingIconColor = AppTheme.colors.icon.primary,
      unfocusedTrailingIconColor = AppTheme.colors.icon.primary,
    )
  }
}

fun Modifier.clearFocusOnKeyboardDismiss(): Modifier = composed {
  var isFocused by remember { mutableStateOf(false) }
  var keyboardAppearedSinceLastFocused by remember { mutableStateOf(false) }
  val density = LocalDensity.current
  val ime = WindowInsets.imeTarget
  val imeIsVisible = remember(ime, density) { derivedStateOf { ime.getBottom(density) > 0 } }.value
  if (isFocused) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(imeIsVisible) {
      if (imeIsVisible) {
        keyboardAppearedSinceLastFocused = true
      } else if (keyboardAppearedSinceLastFocused) {
        focusManager.clearFocus()
      }
    }
  }
  onFocusEvent {
    if (isFocused != it.isFocused) {
      isFocused = it.isFocused
      if (isFocused) {
        keyboardAppearedSinceLastFocused = false
      }
    }
  }
}

@Composable
private fun TextFieldColors.textColor(focused: Boolean): State<Color> {
  return rememberUpdatedState(if (focused) focusedTextColor else unfocusedTextColor)
}

@Composable
private fun TextFieldColors.containerColor(focused: Boolean): State<Color> {
  return rememberUpdatedState(if (focused) focusedContainerColor else unfocusedContainerColor)
}

@Composable
private fun TextFieldColors.indicatorColor(focused: Boolean): State<Color> {
  return rememberUpdatedState(if (focused) focusedIndicatorColor else unfocusedIndicatorColor)
}

@Composable
private fun TextFieldColors.placeholderColor(focused: Boolean): State<Color> {
  return rememberUpdatedState(if (focused) focusedPlaceholderColor else unfocusedPlaceholderColor)
}

@Composable
private fun TextFieldColors.trailingIconColor(focused: Boolean): State<Color> {
  return rememberUpdatedState(if (focused) focusedTrailingIconColor else unfocusedTrailingIconColor)
}

@Immutable
internal data class TextFieldPreviewState(
  val label: String,
  val text: String,
  val focused: Boolean,
)

internal class TextFieldPreviewStateProvider : PreviewParameterProvider<TextFieldPreviewState> {
  override val values = sequenceOf(
    TextFieldPreviewState(
      label = "default",
      text = "",
      focused = false,
    ),
    TextFieldPreviewState(
      label = "focused",
      text = "",
      focused = true,
    ),
    TextFieldPreviewState(
      label = "typing",
      text = "Мос",
      focused = true,
    ),
    TextFieldPreviewState(
      label = "typed",
      text = "Москва",
      focused = true,
    ),
    TextFieldPreviewState(
      label = "filled",
      text = "Москва",
      focused = false,
    ),
  )
}
