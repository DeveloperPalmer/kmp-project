package com.kmpproject.core.routing.flow.decompose

sealed interface FlowTransition<out Config : Any> {
  data class NavigateTo<out Config : Any>(val config: Config) : FlowTransition<Config>
  data object Back : FlowTransition<Nothing>
  data object Ignore : FlowTransition<Nothing>
  data object Stay : FlowTransition<Nothing>
}
