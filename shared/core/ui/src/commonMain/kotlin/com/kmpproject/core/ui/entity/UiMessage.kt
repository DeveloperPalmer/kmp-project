package com.kmpproject.core.ui.entity

import androidx.compose.runtime.Immutable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

@Immutable
data class UiMessage(
  val title: StringResource,
  val description: StringResource? = null,
  val illustration: DrawableResource? = null,
)
