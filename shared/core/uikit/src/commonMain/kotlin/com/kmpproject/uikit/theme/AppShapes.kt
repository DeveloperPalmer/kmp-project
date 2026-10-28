package com.kmpproject.uikit.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class AppShapes(
  val small: Shape = RoundedCornerShape(12.dp),
  val regular: Shape = RoundedCornerShape(16.dp),
  val semiMedium: Shape = RoundedCornerShape(20.dp),
  val sheet: Shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
  val circle: Shape = CircleShape
)

internal val LocalAppShapes = compositionLocalOf<AppShapes> { error("no shapes provided") }
