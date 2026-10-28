package com.kmpproject.feature.cities.ui.screen.cities.recycler

import androidx.recyclerview.widget.RecyclerView
import com.kmpproject.feature.cities.ui.views.databinding.ItemCityHeaderBinding
import com.kmpproject.uikit.view.ViewAppTheme
import com.kmpproject.uikit.view.applyStyle

/**
 * Держатель заголовка группы. У заголовка своя разметка, поэтому и держатель свой: RecyclerView переиспользует
 * держатели только внутри одного viewType, и строка города никогда не получит держатель заголовка
 */
internal class CityHeaderViewHolder(
  private val binding: ItemCityHeaderBinding,
  theme: ViewAppTheme,
) : RecyclerView.ViewHolder(binding.root) {
  init {
    // Как и у строки города, всё, что не зависит от данных, — один раз при создании
    binding.title.applyStyle(
      theme.typography.label,
      theme.colors.text.secondary
    )
  }

  fun bind(group: PopulationGroup) {
    binding.title.setText(group.title)
  }
}
