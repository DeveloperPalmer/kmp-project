package com.kmpproject.feature.map.ui.routing

import com.kmpproject.core.ui.routing.Event

sealed interface FlowEvent : Event {
  data class CitySearchRequested(val cityName: String) : FlowEvent
}
