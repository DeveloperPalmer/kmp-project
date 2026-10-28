package com.kmpproject.uikit.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@Immutable
data class AppShadows(
  val focus: List<Shadow> = listOf(
    Shadow(
      radius = 8.dp,
      color = Color.Black.copy(alpha = 0.12f),
      offset = DpOffset(0.dp, 2.dp)
    )
  ),
  val pin: List<Shadow> = listOf(
    Shadow(
      radius = 3.dp,
      color = Color.Black.copy(alpha = 0.15f),
      offset = DpOffset(0.dp, 2.dp)
    ),
    Shadow(
      radius = 2.dp,
      color = Color.Black.copy(alpha = 0.1f),
      offset = DpOffset(0.dp, 1.dp)
    )
  ),
  val label: List<Shadow> = listOf(
    Shadow(
      radius = 16.dp,
      color = Color.Black.copy(alpha = 0.08f),
      offset = DpOffset(0.dp, 4.dp)
    ),
    Shadow(
      radius = 6.dp,
      color = Color.Black.copy(alpha = 0.08f)
    )
  ),
  val bottomBar: List<Shadow> = listOf(
    Shadow(
      radius = 6.dp,
      color = Color.Black.copy(alpha = 0.15f)
    )
  ),
  val sheet: List<Shadow> = listOf(
    Shadow(
      radius = 6.dp,
      color = Color.Black.copy(alpha = 0.15f)
    )
  )
)

internal val LocalAppShadows = compositionLocalOf<AppShadows> { error("no shadow provided") }

fun Modifier.dropShadow(shadows: List<Shadow>, shape: Shape): Modifier {
  return shadows.fold(this) { modifier, shadow -> modifier.dropShadow(shape, shadow) }
}
