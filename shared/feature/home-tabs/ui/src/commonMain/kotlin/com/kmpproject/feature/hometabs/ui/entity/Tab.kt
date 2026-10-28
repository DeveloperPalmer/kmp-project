package com.kmpproject.feature.hometabs.ui.entity

import com.kmpproject.resources.Res
import com.kmpproject.resources.cities_screen_title
import com.kmpproject.resources.ic_list_24
import com.kmpproject.resources.ic_map_24
import com.kmpproject.resources.map_screen_title
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class Tab(
  val icon: DrawableResource,
  val title: StringResource
) {
  Cities(
    icon = Res.drawable.ic_list_24,
    title = Res.string.cities_screen_title
  ),
  Map(
    icon = Res.drawable.ic_map_24,
    title = Res.string.map_screen_title
  ),
}
