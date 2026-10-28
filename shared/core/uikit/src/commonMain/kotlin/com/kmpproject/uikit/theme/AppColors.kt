package com.kmpproject.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
  val isLight: Boolean,
  val accent: Accent,
  val background: Background,
  val button: Button,
  val divider: Divider,
  val dragger: Color,
  val icon: Icon,
  val pins: Pins,
  val skeleton: Skeleton,
  val text: Text,
) {
  @Immutable
  data class Accent(
    val brand: Color,
    val destructive: Color,
  )

  @Immutable
  data class Background(
    val brand: Color,
    val primary: Color,
    val tertiary: Color,
  )

  @Immutable
  data class Button(
    val primaryBackground: Color,
    val primaryBackgroundPressed: Color,
    val primaryForeground: Color,
    val secondaryForeground: Color,
  )

  @Immutable
  data class Divider(
    val primary: Color,
  )

  @Immutable
  data class Icon(
    val invert: Color,
    val primary: Color,
    val secondary: Color,
  )

  @Immutable
  data class Pins(
    val foreground: Color,
    val primaryBackground: Color,
    val stroke: Color,
  )

  @Immutable
  data class Skeleton(
    val background: Color,
    val highlight: Color,
  )

  @Immutable
  data class Text(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
  )
}
