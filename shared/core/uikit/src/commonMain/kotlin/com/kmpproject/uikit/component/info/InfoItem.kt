package com.kmpproject.uikit.component.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kmpproject.uikit.theme.AppTheme

@Composable
fun InfoItem(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 11.dp)
  ) {
    Text(
      text = label,
      style = AppTheme.typography.subtitle1,
      color = AppTheme.colors.text.primary
    )
    Text(
      text = value,
      style = AppTheme.typography.body1,
      color = AppTheme.colors.text.primary
    )
  }
}

@Preview
@Composable
private fun InfoItemPreviewLight() {
  AppTheme(useDarkTheme = false) {
    InfoItemPreviewContent()
  }
}

@Preview
@Composable
private fun InfoItemPreviewDark() {
  AppTheme(useDarkTheme = true) {
    InfoItemPreviewContent()
  }
}

@Composable
private fun InfoItemPreviewContent() {
  InfoItem(
    modifier = Modifier
      .background(AppTheme.colors.background.primary)
      .padding(horizontal = 16.dp),
    label = "Город",
    value = "Москва",
  )
}
