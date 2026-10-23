package com.kmpbaseproject.feature.cities.ui.screen.cities.recycler

import android.content.res.ColorStateList
import androidx.compose.ui.graphics.toArgb
import androidx.recyclerview.widget.RecyclerView
import com.kmpbaseproject.feature.cities.domain.entity.City
import com.kmpbaseproject.feature.cities.ui.views.databinding.ItemCityBinding
import com.kmpbaseproject.uikit.view.ViewAppTheme
import com.kmpbaseproject.uikit.view.applyStyle

/**
 * Держатель одной строки списка: её View и ссылки на нужные дочерние View, чтобы не искать их при каждой привязке.
 * RecyclerView создаёт держателей примерно столько, сколько строк помещается на экране, плюс несколько про запас,
 * и дальше только переиспользует их: строка, ушедшая за край при прокрутке, попадает в пул и получает через [bind]
 * данные другого города
 */
internal class CityItemViewHolder(
  private val binding: ItemCityBinding,
  theme: ViewAppTheme,
  onClick: (City) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
  // Город, который строка показывает сейчас: у переиспользованного держателя он меняется
  private var city: City? = null

  init {
    // Всё, что не зависит от данных, делается один раз при создании держателя, а не в каждом bind
    binding.title.applyStyle(
      theme.typography.subtitle1,
      theme.colors.text.primary
    )
    binding.pin.imageTintList = ColorStateList.valueOf(
      theme.colors.icon.secondary.toArgb()
    )
    // Слушатель тоже один на держателя: в bind он создавался бы заново при каждом появлении строки. Город берётся
    // в момент нажатия. Захватить position из onBindViewHolder было бы ошибкой: после вставки или удаления строк
    // выше он устаревает. Если нужна позиция, её читают в момент нажатия через bindingAdapterPosition
    binding.root.setOnClickListener { city?.let(onClick) }
  }

  fun bind(item: City) {
    city = item
    binding.title.text = item.title
  }
}
