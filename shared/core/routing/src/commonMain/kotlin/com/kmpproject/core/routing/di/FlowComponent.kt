package com.kmpproject.core.routing.di

import androidx.compose.runtime.Stable
import com.kmpproject.core.ui.viewmodel.AssistedViewModelProvider
import com.kmpproject.core.ui.viewmodel.ViewModelProvider
import kotlinx.coroutines.CoroutineScope

@Stable
interface FlowComponent {
  // VMs registered in a given FlowScope
  fun viewModelProviders(): Set<ViewModelProvider>
  fun assistedViewModelProviders(): Set<AssistedViewModelProvider>

  // VMs registered in a given FlowScope + its parent FlowScopes
  fun flowViewModelProviders(): Set<ViewModelProvider>
  fun assistedFlowViewModelProviders(): Set<AssistedViewModelProvider>

  fun coroutineScope(): CoroutineScope
}
