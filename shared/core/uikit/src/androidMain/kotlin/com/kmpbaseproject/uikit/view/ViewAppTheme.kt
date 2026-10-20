package com.kmpbaseproject.uikit.view

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import com.kmpbaseproject.uikit.theme.AppColors
import com.kmpbaseproject.uikit.theme.AppShapes
import com.kmpbaseproject.uikit.theme.AppTypography
import com.kmpbaseproject.uikit.theme.DarkAppColors
import com.kmpbaseproject.uikit.theme.LightAppColors

/**
 * AppTheme for Android Views. Views cannot read the Compose theme, so they take the same design tokens directly:
 * a color changed in ColorPalette changes both the Compose UI and the views
 */
class ViewAppTheme(context: Context) {
  // The same choice as isSystemInDarkTheme() makes in AppTheme
  val colors: AppColors = if (context.isNightMode()) DarkAppColors else LightAppColors
  val typography = AppTypography()
  val shapes = AppShapes()

  private val density = Density(context)

  fun cornerRadius(shape: Shape): Float {
    // The theme shapes are rounded corners in dp, so the size of the view does not matter
    return (shape as CornerBasedShape).topStart.toPx(Size.Unspecified, density)
  }
}

private fun Context.isNightMode(): Boolean {
  val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
  return nightMode == Configuration.UI_MODE_NIGHT_YES
}
