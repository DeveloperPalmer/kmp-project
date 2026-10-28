package com.kmpproject.core.routing.flow.decompose

import com.arkivanov.decompose.ComponentContext
import com.kmpproject.core.routing.di.FlowComponent
import com.kmpproject.core.ui.mvi.BaseViewModel

// The context keeps the view model while it lives: after the Activity recreation
// the same context key returns the same instance instead of building a new one
inline fun <reified VM : BaseViewModel<*, *>> FlowComponent.viewModel(
  context: ComponentContext,
  vararg params: Any?,
): VM {
  val key = VM::class.qualifiedName.orEmpty()
  return context.retainedViewModel(VM::class) {
    if (params.isEmpty()) {
      flowViewModelProviders()
        .firstOrNull { it.key == key }
        ?.factory
        ?.invoke()
    } else {
      assistedFlowViewModelProviders()
        .firstOrNull { it.key == key }
        ?.factory
        ?.build(*params)
    }
      as? VM
      ?: error("ViewModel $key is not registered")
  }
}
