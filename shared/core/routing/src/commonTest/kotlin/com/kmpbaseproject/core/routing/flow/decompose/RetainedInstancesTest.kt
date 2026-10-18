package com.kmpbaseproject.core.routing.flow.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.kmpbaseproject.core.routing.di.FlowComponent
import com.kmpbaseproject.core.ui.mvi.BaseViewModel
import com.kmpbaseproject.core.ui.viewmodel.AssistedViewModelProvider
import com.kmpbaseproject.core.ui.viewmodel.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import org.orbitmvi.orbit.orbitContainer
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

// The instance keeper plays the Activity here: it stays when the lifecycle of a context is destroyed
class RetainedInstancesTest {
  private val keeper = InstanceKeeperDispatcher()

  @Test
  fun `returns the same view model to a context created after recreation`() {
    val first = recreate().retainedViewModel(RetainedViewModel::class) { RetainedViewModel() }

    val second = recreate().retainedViewModel(RetainedViewModel::class) { RetainedViewModel() }

    assertSame(first, second)
  }

  @Test
  fun `keeps view model alive when lifecycle is destroyed`() {
    val lifecycle = LifecycleRegistry().apply { resume() }
    val viewModel = context(lifecycle).retainedViewModel(RetainedViewModel::class) { RetainedViewModel() }

    lifecycle.destroy()

    assertTrue(viewModel.viewModelScope.isActive)
  }

  @Test
  fun `clears view model when instance keeper is destroyed`() {
    val viewModel = recreate().retainedViewModel(RetainedViewModel::class) { RetainedViewModel() }

    keeper.destroy()

    assertFalse(viewModel.viewModelScope.isActive)
  }

  @Test
  fun `returns the same flow component to a context created after recreation`() {
    val first = recreate().retainedFlowComponent { RetainedFlowComponent() }

    val second = recreate().retainedFlowComponent { RetainedFlowComponent() }

    assertSame(first, second)
  }

  @Test
  fun `keeps flow component scope alive when lifecycle is destroyed`() {
    val lifecycle = LifecycleRegistry().apply { resume() }
    val component = context(lifecycle).retainedFlowComponent { RetainedFlowComponent() }

    lifecycle.destroy()

    assertTrue(component.coroutineScope().isActive)
  }

  @Test
  fun `cancels flow component scope when instance keeper is destroyed`() {
    val component = recreate().retainedFlowComponent { RetainedFlowComponent() }

    keeper.destroy()

    assertFalse(component.coroutineScope().isActive)
  }

  private fun recreate(): ComponentContext = context(LifecycleRegistry().apply { resume() })

  private fun context(lifecycle: LifecycleRegistry): ComponentContext {
    return DefaultComponentContext(lifecycle = lifecycle, instanceKeeper = keeper)
  }
}

private class RetainedViewModel : BaseViewModel<Unit, Nothing>() {
  override val container = viewModelScope.orbitContainer<Unit, Nothing>(Unit)
}

private class RetainedFlowComponent : FlowComponent {
  private val scope = CoroutineScope(Job())

  override fun viewModelProviders(): Set<ViewModelProvider> = emptySet()

  override fun assistedViewModelProviders(): Set<AssistedViewModelProvider> = emptySet()

  override fun flowViewModelProviders(): Set<ViewModelProvider> = emptySet()

  override fun assistedFlowViewModelProviders(): Set<AssistedViewModelProvider> = emptySet()

  override fun coroutineScope(): CoroutineScope = scope
}
