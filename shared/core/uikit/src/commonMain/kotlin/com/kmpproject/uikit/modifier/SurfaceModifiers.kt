package com.kmpproject.uikit.modifier

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.semantics.Role
import com.kmpproject.uikit.theme.dropShadow

@Suppress("LongParameterList")
fun Modifier.surface(
  backgroundColor: Color,
  shape: Shape,
  border: BorderStroke? = null,
  enabled: Boolean = true,
  role: Role? = null,
  shadows: List<Shadow> = emptyList(),
  interactionSource: MutableInteractionSource? = null,
  onClick: (() -> Unit)? = null,
  onLongClick: (() -> Unit)? = null,
): Modifier = surface(
  backgroundColor = { backgroundColor },
  shape = shape,
  border = border,
  enabled = enabled,
  role = role,
  shadows = shadows,
  interactionSource = interactionSource,
  onClick = onClick,
  onLongClick = onLongClick,
)

@Suppress("LongParameterList")
fun Modifier.surface(
  backgroundColor: () -> Color,
  shape: Shape,
  border: BorderStroke? = null,
  enabled: Boolean = true,
  role: Role? = null,
  shadows: List<Shadow> = emptyList(),
  interactionSource: MutableInteractionSource? = null,
  onClick: (() -> Unit)? = null,
  onLongClick: (() -> Unit)? = null,
): Modifier {
  return this
    .then(if (shadows.isNotEmpty()) Modifier.dropShadow(shadows, shape) else Modifier)
    .then(if (border != null) Modifier.border(border, shape) else Modifier)
    .drawWithCache {
      val outline = shape.createOutline(
        size = size,
        density = this,
        layoutDirection = layoutDirection
      )
      onDrawBehind { drawOutline(outline, backgroundColor()) }
    }
    .clip(shape = shape)
    .then(
      if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(
          enabled = enabled,
          role = role,
          onClick = { onClick?.invoke() },
          onLongClick = onLongClick,
          interactionSource = interactionSource,
        )
      } else {
        Modifier
      },
    )
}
