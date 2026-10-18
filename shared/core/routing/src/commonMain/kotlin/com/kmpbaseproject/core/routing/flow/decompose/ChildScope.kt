package com.kmpbaseproject.core.routing.flow.decompose

import com.arkivanov.decompose.ComponentContext
import com.kmpbaseproject.core.routing.di.FlowComponent
import com.kmpbaseproject.core.ui.mvi.BaseViewModel

class ChildScope(
  val context: ComponentContext,
  @PublishedApi
  internal val owner: FlowComponent,
) {
  inline fun <reified VM : BaseViewModel<*, *>> viewModel(vararg params: Any?): VM {
    return owner.viewModel<VM>(context, *params)
  }

  fun <C : FlowComponent> flowComponent(create: () -> C): C {
    return context.retainedFlowComponent(create)
  }
}
