package com.kmpproject.feature.hometabs.routing

import androidx.compose.runtime.Stable
import com.kmpproject.core.routing.di.FlowComponent
import com.kmpproject.feature.cities.routing.CitiesFlowComponent
import com.kmpproject.feature.hometabs.domain.di.HomeTabsScope
import com.kmpproject.feature.map.routing.MapFlowComponent
import com.kmpproject.lib.annotation.MergeSubcomponent

@Stable
@MergeSubcomponent(HomeTabsScope::class)
interface HomeTabsFlowComponent : FlowComponent {
  fun citiesFlowComponent(): CitiesFlowComponent
  fun mapFlowComponent(): MapFlowComponent
}
