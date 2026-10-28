package com.kmpproject.feature.cities.routing

import androidx.compose.runtime.Stable
import com.kmpproject.core.routing.di.FlowComponent
import com.kmpproject.feature.cities.domain.di.CitiesScope
import com.kmpproject.lib.annotation.MergeSubcomponent

@Stable
@MergeSubcomponent(CitiesScope::class)
interface CitiesFlowComponent : FlowComponent
