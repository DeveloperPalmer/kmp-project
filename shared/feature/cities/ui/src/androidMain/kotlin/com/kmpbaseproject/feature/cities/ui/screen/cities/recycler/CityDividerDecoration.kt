package com.kmpbaseproject.feature.cities.ui.screen.cities.recycler

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

/**
 * Линия между городами, как HorizontalDivider в Compose-версии: между двумя городами подряд. Над заголовком группы
 * и сразу под ним линии нет: группы и так разделяет заголовок.
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

  override fun getItemOffsets(
    outRect: Rect,
    view: View,
    parent: RecyclerView,
    state: RecyclerView.State
  ) {
    // Место под линию сверху — только у строк, над которыми линия будет
    if (hasDividerAbove(view, parent)) {
      outRect.top = thickness
    }
  }

  override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    // Детей у RecyclerView столько, сколько строк сейчас на экране, а не сколько городов в списке
    for (index in 0 until parent.childCount) {
      val child = parent.getChildAt(index)
      if (!hasDividerAbove(child, parent)) continue
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

  // Линия над строкой — только если и она, и строка над ней — города. Позиция и адаптер берутся у ViewHolder:
  // bindingAdapterPosition — позиция в своём адаптере. Она останется верной, даже если список соберут из нескольких
  // адаптеров через ConcatAdapter, а getChildAdapterPosition вернул бы позицию во всём списке
  private fun hasDividerAbove(view: View, parent: RecyclerView): Boolean {
    val holder = parent.getChildViewHolder(view)
    if (holder !is CityItemViewHolder) {
      return false
    }
    val adapter = holder.bindingAdapter as? CitiesAdapter ?: return false
    // NO_POSITION (-1) — строка уже удалена и доигрывает анимацию
    val position = holder.bindingAdapterPosition
    return position > 0 && adapter.isCityAt(position - 1)
  }
}
