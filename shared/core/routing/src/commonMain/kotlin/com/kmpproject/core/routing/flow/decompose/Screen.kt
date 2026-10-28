package com.kmpproject.core.routing.flow.decompose

import com.kmpproject.core.ui.mvi.BaseViewModel

interface Node

interface Screen : Node {
  val viewModel: BaseViewModel<*, *>
}

interface Flow : Node {
  val component: FlowNode<*>
}
