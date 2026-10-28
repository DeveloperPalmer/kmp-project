package com.kmpproject.feature.app.routing

import androidx.compose.runtime.Stable
import com.kmpproject.core.domain.di.AppFlowScope
import com.kmpproject.core.routing.di.FlowComponent
import com.kmpproject.core.ui.viewmodel.AssistedViewModelProvider
import com.kmpproject.core.ui.viewmodel.ViewModelProvider
import com.kmpproject.core.ui.viewmodel.emptyAssistedViewModelProvider
import com.kmpproject.core.ui.viewmodel.emptyViewModelProvider
import com.kmpproject.feature.hometabs.routing.HomeTabsFlowComponent
import com.kmpproject.lib.annotation.MergeSubcomponent
import com.kmpproject.lib.annotation.ScopedViewModel
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

@Stable
@MergeSubcomponent(AppFlowScope::class)
interface AppFlowComponent : FlowComponent {
  fun homeTabsFlowComponent(): HomeTabsFlowComponent

  @Provides
  @IntoSet
  fun emptyAssistedViewModelProviders(): @ScopedViewModel(AppFlowScope::class) AssistedViewModelProvider {
    return emptyAssistedViewModelProvider
  }

  @Provides
  @IntoSet
  fun emptyViewModelProviders(): @ScopedViewModel(AppFlowScope::class) ViewModelProvider {
    return emptyViewModelProvider
  }
}
