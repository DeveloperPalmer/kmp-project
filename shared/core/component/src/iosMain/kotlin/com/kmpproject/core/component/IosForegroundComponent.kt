package com.kmpproject.core.component

import com.kmpproject.core.domain.di.ForegroundScope
import com.kmpproject.feature.app.routing.AppFlowComponent
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(ForegroundScope::class)
@ContributesSubcomponent(ForegroundScope::class)
interface IosForegroundComponent : ForegroundComponent {
  @ContributesSubcomponent.Factory(AppScope::class)
  interface Factory {
    fun create(): IosForegroundComponent
  }

  override fun appFlowComponent(): AppFlowComponent
}
