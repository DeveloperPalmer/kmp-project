package com.kmpproject.uikit.component.shimmer

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.util.lerp
import com.kmpproject.uikit.theme.AppTheme

fun Modifier.shimmer(shape: Shape): Modifier = composed {
  val color = AppTheme.colors.skeleton.background
  val highlightColor = AppTheme.colors.skeleton.highlight
  val progress = rememberInfiniteTransition(label = "shimmer").animateFloat(
    label = "shimmerProgress",
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = ShimmerAnimationSpec,
  )
  drawWithCache {
    val outline = shape.createOutline(size, layoutDirection, this)
    val waveWidth = size.width * WAVE_WIDTH_FRACTION
    val waveColors = listOf(
      highlightColor.copy(alpha = 0f),
      highlightColor,
      highlightColor.copy(alpha = 0f)
    )
    onDrawWithContent {
      val waveStart = lerp(-waveWidth, size.width, progress.value)
      drawOutline(
        outline = outline,
        color = color
      )
      drawOutline(
        outline = outline,
        brush = Brush.horizontalGradient(
          colors = waveColors,
          startX = waveStart,
          endX = waveStart + waveWidth,
        ),
      )
    }
  }
}

private val ShimmerAnimationSpec: InfiniteRepeatableSpec<Float> = infiniteRepeatable(
  animation = tween(durationMillis = 1000, delayMillis = 200),
  repeatMode = RepeatMode.Restart,
)

private const val WAVE_WIDTH_FRACTION = 0.5f
