package com.kmpbaseproject.feature.cities.ui.screen.cities

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * Линия между городами, как HorizontalDivider в Compose-версии: над каждой строкой, кроме первой.
 *
 * ItemDecoration украшает список, не трогая разметку строк. getItemOffsets оставляет вокруг строки место,
 * onDraw рисует на Canvas самого RecyclerView под строками, а onDrawOver — поверх них
 */
internal class CityDividerDecoration(
  color: Int,
  private val thickness: Int,
) : RecyclerView.ItemDecoration() {
  private val paint = Paint().apply { this.color = color }

  // Переиспользуемый прямоугольник: onDraw вызывается на каждом кадре прокрутки, и создавать объекты в нём нельзя
  private val bounds = Rect()

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    // Место под линию сверху у всех строк, кроме первой. Позиция берётся в адаптере, а не среди видимых детей
    if (parent.getChildAdapterPosition(view) > 0) {
      outRect.top = thickness
    }
  }

  override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    // Детей у RecyclerView столько, сколько строк сейчас на экране, а не сколько городов в списке
    for (index in 0 until parent.childCount) {
      val child = parent.getChildAt(index)
      // NO_POSITION (-1) — строка уже удалена и доигрывает анимацию
      if (parent.getChildAdapterPosition(child) <= 0) continue
      // Границы строки вместе с отступом, который оставил getItemOffsets; translationY — сдвиг анимации
      parent.getDecoratedBoundsWithMargins(child, bounds)
      val top = bounds.top + child.translationY.roundToInt()
      canvas.drawRect(
        bounds.left.toFloat(),
        top.toFloat(),
        bounds.right.toFloat(),
        (top + thickness).toFloat(),
        paint,
      )
    }
  }
}
