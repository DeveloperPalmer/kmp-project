package com.kmpbaseproject.core.routing.flow.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.kmpbaseproject.core.routing.di.FlowComponent
import com.kmpbaseproject.core.ui.mvi.BaseViewModel
import com.kmpbaseproject.core.ui.viewmodel.AssistedViewModelProvider
import com.kmpbaseproject.core.ui.viewmodel.ViewModelProvider
import com.kmpbaseproject.core.ui.viewmodel.emptyAssistedViewModelProvider
import com.kmpbaseproject.core.ui.viewmodel.emptyViewModelProvider
import kotlinx.coroutines.CoroutineScope
import org.orbitmvi.orbit.orbitContainer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class FlowComponentViewModelTest {
  private val component = TestFlowComponent(
    flowViewModelProviders = setOf(
      emptyViewModelProvider,
      ViewModelProvider(MainViewModel::class.qualifiedName.orEmpty()) { MainViewModel() },
    ),
    assistedFlowViewModelProviders = setOf(
      emptyAssistedViewModelProvider,
      AssistedViewModelProvider(DetailsViewModel::class.qualifiedName.orEmpty()) { params ->
        DetailsViewModel(title = params[0] as String, id = params[1] as Int)
      },
    ),
  )

  private val context = newContext()

  @Test
  fun `finds view model registered in flow or its parent flows`() {
    assertIs<MainViewModel>(component.viewModel<MainViewModel>(context))
  }

  @Test
  fun `returns the same view model for the same context`() {
    assertSame(component.viewModel<MainViewModel>(context), component.viewModel<MainViewModel>(context))
  }

  @Test
  fun `creates own view model for each context`() {
    assertNotSame(component.viewModel<MainViewModel>(context), component.viewModel<MainViewModel>(newContext()))
  }

  @Test
  fun `passes params to assisted factory in order`() {
    val details = component.viewModel<DetailsViewModel>(context, "Scooter", 42)

    assertEquals("Scooter", details.title)
    assertEquals(42, details.id)
  }

  @Test
  fun `fails for view model that is not registered`() {
    val error = assertFailsWith<IllegalStateException> { component.viewModel<UnknownViewModel>(context) }

    assertEquals("ViewModel ${UnknownViewModel::class.qualifiedName} is not registered", error.message)
  }

  @Test
  fun `does not mix plain and assisted view models`() {
    val plainWithParams =
      assertFailsWith<IllegalStateException> { component.viewModel<MainViewModel>(context, "Scooter") }
    val assistedWithoutParams =
      assertFailsWith<IllegalStateException> { component.viewModel<DetailsViewModel>(context) }

    assertEquals("ViewModel ${MainViewModel::class.qualifiedName} is not registered", plainWithParams.message)
    assertEquals("ViewModel ${DetailsViewModel::class.qualifiedName} is not registered", assistedWithoutParams.message)
  }
}

private fun newContext(): ComponentContext = DefaultComponentContext(LifecycleRegistry())

// Only the merged sets are filled: they alone carry view models of parent flows
private class TestFlowComponent(
  private val flowViewModelProviders: Set<ViewModelProvider>,
  private val assistedFlowViewModelProviders: Set<AssistedViewModelProvider>,
) : FlowComponent {
  override fun viewModelProviders(): Set<ViewModelProvider> = emptySet()

  override fun assistedViewModelProviders(): Set<AssistedViewModelProvider> = emptySet()

  override fun flowViewModelProviders(): Set<ViewModelProvider> = flowViewModelProviders

  override fun assistedFlowViewModelProviders(): Set<AssistedViewModelProvider> = assistedFlowViewModelProviders

  override fun coroutineScope(): CoroutineScope = error("not used by viewModel()")
}

private class MainViewModel : BaseViewModel<Unit, Nothing>() {
  override val container = viewModelScope.orbitContainer<Unit, Nothing>(Unit)
}

private class DetailsViewModel(
  val title: String,
  val id: Int,
) : BaseViewModel<Unit, Nothing>() {
  override val container = viewModelScope.orbitContainer<Unit, Nothing>(Unit)
}

private class UnknownViewModel : BaseViewModel<Unit, Nothing>() {
  override val container = viewModelScope.orbitContainer<Unit, Nothing>(Unit)
}
