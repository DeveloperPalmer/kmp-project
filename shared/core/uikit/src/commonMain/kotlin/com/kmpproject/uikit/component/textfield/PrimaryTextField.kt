package com.kmpproject.uikit.component.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.kmpproject.resources.Res
import com.kmpproject.resources.ic_search_24
import com.kmpproject.uikit.theme.AppTheme
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun PrimaryTextField(
  value: String,
  onValueChange: (String) -> Unit,
  placeholder: String,
  trailingIcon: DrawableResource,
  trailingIconDescription: String,
  modifier: Modifier = Modifier,
) {
  TextFieldInternal(
    modifier = modifier,
    value = value,
    onValueChange = onValueChange,
    shape = AppTheme.shapes.regular,
    placeholder = placeholder,
    trailingIcon = trailingIcon,
    trailingIconDescription = trailingIconDescription,
    colors = TextFieldDefaults.primaryDefaultColors(),
  )
}

@Preview
@Composable
private fun PrimaryTextFieldPreviewLight(
  @PreviewParameter(TextFieldPreviewStateProvider::class)
  state: TextFieldPreviewState,
) {
  AppTheme(useDarkTheme = false) {
    PrimaryTextFieldPreviewContent(state)
  }
}

@Preview
@Composable
private fun PrimaryTextFieldPreviewDark(
  @PreviewParameter(TextFieldPreviewStateProvider::class)
  state: TextFieldPreviewState,
) {
  AppTheme(useDarkTheme = true) {
    PrimaryTextFieldPreviewContent(state)
  }
}

@Composable
private fun PrimaryTextFieldPreviewContent(state: TextFieldPreviewState) {
  Box(
    modifier = Modifier
      .background(AppTheme.colors.background.primary)
      .padding(16.dp),
  ) {
    TextFieldInternal(
      modifier = Modifier.fillMaxWidth(),
      value = state.text,
      onValueChange = {},
      shape = AppTheme.shapes.regular,
      placeholder = "Введите название города",
      trailingIcon = Res.drawable.ic_search_24,
      trailingIconDescription = "search icon",
      colors = TextFieldDefaults.primaryDefaultColors(),
    )
  }
}
