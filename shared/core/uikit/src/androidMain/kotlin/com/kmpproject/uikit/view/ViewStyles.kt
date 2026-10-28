package com.kmpproject.uikit.view

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import kotlin.math.roundToInt

/**
 * Applies a text style of AppTypography and a color to a TextView
 */
fun TextView.applyStyle(style: TextStyle, color: Color) {
  setTextColor(color.toArgb())
  setTextSize(TypedValue.COMPLEX_UNIT_SP, style.fontSize.value)
  typeface = style.fontWeight.toTypeface()
  // Before Android 9 a TextView has no exact line height, the default spacing stays
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    lineHeight = TypedValue
      .applyDimension(TypedValue.COMPLEX_UNIT_SP, style.lineHeight.value, resources.displayMetrics)
      .roundToInt()
  }
}

/**
 * Styles a Button as the primary button of the design system, PrimaryButton in Compose
 */
fun Button.applyPrimaryStyle(theme: ViewAppTheme) {
  val colors = theme.colors.button
  val radius = theme.cornerRadius(theme.shapes.regular)
  background = StateListDrawable().apply {
    addState(intArrayOf(android.R.attr.state_pressed), roundedRect(colors.primaryBackgroundPressed, radius))
    addState(intArrayOf(), roundedRect(colors.primaryBackground, radius))
  }
  applyStyle(theme.typography.title4, colors.primaryForeground)
  isAllCaps = false
  // The framework button raises itself with a shadow, the design system button is flat. Its animator has set
  // the elevation already when the view was inflated, so dropping the animator alone keeps the shadow
  stateListAnimator = null
  elevation = 0f
  translationZ = 0f
}

private fun roundedRect(color: Color, radius: Float): GradientDrawable {
  return GradientDrawable().apply {
    setColor(color.toArgb())
    cornerRadius = radius
  }
}

private fun FontWeight?.toTypeface(): Typeface {
  val weight = (this ?: FontWeight.Normal).weight
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    Typeface.create(Typeface.DEFAULT, weight, false)
  } else {
    // Before Android 9 a typeface has only normal and bold
    Typeface.create(Typeface.DEFAULT, if (weight >= FontWeight.SemiBold.weight) Typeface.BOLD else Typeface.NORMAL)
  }
}
