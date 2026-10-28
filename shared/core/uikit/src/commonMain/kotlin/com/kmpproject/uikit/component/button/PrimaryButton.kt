package com.kmpproject.uikit.component.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kmpproject.uikit.theme.AppTheme

@Composable
fun PrimaryButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ButtonInternal(
    modifier = modifier,
    text = text,
    colors = ButtonDefaults.primaryDefaultColors(),
    onClick = onClick,
  )
}

@Preview
@Composable
private fun PrimaryButtonPreviewLight() {
  AppTheme(useDarkTheme = false) {
    PrimaryButtonPreviewContent()
  }
}

@Preview
@Composable
private fun PrimaryButtonPreviewDark() {
  AppTheme(useDarkTheme = true) {
    PrimaryButtonPreviewContent()
  }
}

@Composable
private fun PrimaryButtonPreviewContent() {
  Box(
    modifier = Modifier
      .background(AppTheme.colors.background.primary)
      .padding(16.dp),
  ) {
    ButtonInternal(
      modifier = Modifier.fillMaxWidth(),
      text = "Поиск информации о городе",
      colors = ButtonDefaults.primaryDefaultColors(),
      onClick = {},
    )
  }
}
