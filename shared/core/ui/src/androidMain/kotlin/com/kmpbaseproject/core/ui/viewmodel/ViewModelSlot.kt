package com.kmpbaseproject.core.ui.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ViewModelSlot<VM : Any> : ViewModelBinder<VM> {
  private val _viewModel = MutableStateFlow<VM?>(null)
  override val viewModel: StateFlow<VM?> = _viewModel.asStateFlow()

  override fun onBind(viewModel: VM) {
    _viewModel.value = viewModel
  }
}
