package com.kmpproject.feature.map.routing

import androidx.compose.runtime.Stable
import com.kmpproject.core.domain.AppLauncher
import com.kmpproject.core.routing.di.FlowComponent
import com.kmpproject.feature.map.domain.di.MapScope
import com.kmpproject.lib.annotation.MergeSubcomponent

@Stable
@MergeSubcomponent(MapScope::class)
interface MapFlowComponent : FlowComponent {
  fun appLauncher(): AppLauncher
}
