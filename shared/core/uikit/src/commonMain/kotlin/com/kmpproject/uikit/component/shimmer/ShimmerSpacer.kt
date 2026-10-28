package com.kmpproject.uikit.component.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kmpproject.uikit.theme.AppTheme
import com.kmpproject.uikit.theme.VSpacer

@Composable
fun ShimmerSpacer(
  width: Dp,
  height: Dp,
  modifier: Modifier = Modifier,
) {
  Spacer(
    modifier = modifier
      .shimmer(AppTheme.shapes.small)
      .size(width, height),
  )
}

@Preview
@Composable
private fun ShimmerSpacerPreviewLight() {
  AppTheme(useDarkTheme = false) {
    ShimmerSpacerPreviewContent()
  }
}

@Preview
@Composable
private fun ShimmerSpacerPreviewDark() {
  AppTheme(useDarkTheme = true) {
    ShimmerSpacerPreviewContent()
  }
}

@Composable
private fun ShimmerSpacerPreviewContent() {
  Column(
    modifier = Modifier
      .background(AppTheme.colors.background.primary)
      .padding(16.dp),
  ) {
    ShimmerSpacer(width = 144.dp, height = 22.dp)
    VSpacer(16.dp)
    ShimmerSpacer(width = 180.dp, height = 22.dp)
  }
}
