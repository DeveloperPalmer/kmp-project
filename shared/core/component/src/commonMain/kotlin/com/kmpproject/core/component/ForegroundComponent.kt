package com.kmpproject.core.component

import com.kmpproject.core.domain.di.ForegroundScope
import com.kmpproject.core.ui.viewmodel.AssistedViewModelProvider
import com.kmpproject.core.ui.viewmodel.ViewModelProvider
import com.kmpproject.core.ui.viewmodel.emptyAssistedViewModelProvider
import com.kmpproject.core.ui.viewmodel.emptyViewModelProvider
import com.kmpproject.feature.app.routing.AppFlowComponent
import com.kmpproject.lib.annotation.MergedViewModels
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface ForegroundComponent {
  fun appFlowComponent(): AppFlowComponent

  @Provides
  @IntoSet
  fun emptyMergedViewModelProviders(): @MergedViewModels(ForegroundScope::class) ViewModelProvider {
    return emptyViewModelProvider
  }

  @Provides
  @IntoSet
  fun emptyMergedAssistedViewModelProviders(): @MergedViewModels(ForegroundScope::class) AssistedViewModelProvider {
    return emptyAssistedViewModelProvider
  }
}
