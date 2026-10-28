package com.kmpproject.feature.hometabs.ui.screen.home

import androidx.compose.runtime.Immutable
import com.kmpproject.feature.hometabs.ui.entity.Tab

@Immutable
data class ViewState(
  val selectedTab: Tab = Tab.Cities
) {
  val isBackEnabled: Boolean
    get() = selectedTab != Tab.Cities
}
