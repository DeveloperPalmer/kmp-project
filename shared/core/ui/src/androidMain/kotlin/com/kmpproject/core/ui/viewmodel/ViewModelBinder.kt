package com.kmpproject.core.ui.viewmodel

import kotlinx.coroutines.flow.StateFlow

interface ViewModelBinder<VM : Any> {
  val viewModel: StateFlow<VM?>
  fun onBind(viewModel: VM)
}
