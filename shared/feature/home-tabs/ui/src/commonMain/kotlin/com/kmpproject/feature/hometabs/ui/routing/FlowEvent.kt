package com.kmpproject.feature.hometabs.ui.routing

import com.kmpproject.core.ui.routing.Event
import com.kmpproject.feature.hometabs.ui.entity.Tab

sealed interface FlowEvent : Event {
  data class TabChangeRequested(val tab: Tab) : FlowEvent
}
