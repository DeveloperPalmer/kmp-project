package com.kmpproject.core.routing.flow.decompose

import com.kmpproject.core.ui.routing.Event

// A flow that answers events with transitions and passes the ignored ones up to its parent flow
abstract class FlowNode<Config : Any> {
  private var parent: FlowNode<*>? = null

  abstract fun transition(event: Event): FlowTransition<Config>

  protected abstract fun navigateTo(config: Config)

  protected abstract fun navigateBack()

  protected fun adopt(child: Flow) {
    child.component.parent = this
  }

  protected fun dispatch(event: Event) {
    when (val transition = transition(event)) {
      is FlowTransition.Stay -> {}
      is FlowTransition.Ignore -> {
        parent?.dispatch(event)
      }
      is FlowTransition.Back -> {
        navigateBack()
      }
      is FlowTransition.NavigateTo -> {
        navigateTo(transition.config)
      }
    }
  }
}
