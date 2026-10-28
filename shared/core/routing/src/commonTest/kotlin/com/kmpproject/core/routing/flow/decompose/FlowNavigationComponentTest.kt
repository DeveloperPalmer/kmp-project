package com.kmpproject.core.routing.flow.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.kmpproject.core.routing.di.FlowComponent
import com.kmpproject.core.ui.mvi.BaseViewModel
import com.kmpproject.core.ui.routing.Event
import com.kmpproject.core.ui.viewmodel.AssistedViewModelProvider
import com.kmpproject.core.ui.viewmodel.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import org.orbitmvi.orbit.orbitContainer
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class FlowNavigationComponentTest {
  @BeforeTest
  fun setUp() {
    Dispatchers.setMain(UnconfinedTestDispatcher())
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `navigates to config returned by transition`() {
    val flow = TestFlowNavigationComponent()

    flow.activeViewModel().send(TestFlowEvent.DetailsRequested)

    assertEquals(listOf(TestConfig.Root, TestConfig.Details), flow.configs())
  }

  @Test
  fun `passes events of pushed screen to transition`() {
    val flow = TestFlowNavigationComponent()
    flow.activeViewModel().send(TestFlowEvent.DetailsRequested)

    flow.activeViewModel().send(TestFlowEvent.MoreRequested)

    assertEquals(listOf(TestConfig.Root, TestConfig.Details, TestConfig.More), flow.configs())
  }

  @Test
  fun `stops passing events of destroyed screen`() {
    val flow = TestFlowNavigationComponent()
    flow.activeViewModel().send(TestFlowEvent.DetailsRequested)
    val details = flow.activeViewModel()
    flow.back()

    details.send(TestFlowEvent.DetailsRequested)

    assertEquals(listOf(TestConfig.Root), flow.configs())
  }

  @Test
  fun `navigates back on back transition`() {
    val flow = TestFlowNavigationComponent()
    flow.activeViewModel().send(TestFlowEvent.DetailsRequested)

    flow.activeViewModel().send(TestFlowEvent.BackRequested)

    assertEquals(listOf(TestConfig.Root), flow.configs())
  }

  @Test
  fun `keeps event in its flow on stay transition`() {
    val parent = TestParentFlow()
    val nested = parent.nested()

    nested.activeViewModel().send(TestFlowEvent.StayRequested)

    assertEquals(listOf(TestConfig.Root), nested.configs())
    assertEquals(listOf(TestParentConfig.Nested), parent.configs())
  }

  @Test
  fun `passes ignored event to parent flow`() {
    val parent = TestParentFlow()

    parent.nested().activeViewModel().send(TestFlowEvent.ParentRequested)

    assertEquals(listOf(TestParentConfig.Nested, TestParentConfig.Other), parent.configs())
  }

  @Test
  fun `keeps handled event in its flow`() {
    val parent = TestParentFlow()
    val nested = parent.nested()

    nested.activeViewModel().send(TestFlowEvent.DetailsRequested)

    assertEquals(listOf(TestConfig.Root, TestConfig.Details), nested.configs())
    assertEquals(listOf(TestParentConfig.Nested), parent.configs())
  }
}

private fun resumedContext(): ComponentContext = DefaultComponentContext(LifecycleRegistry().apply { resume() })

private class TestFlowNavigationComponent(
  context: ComponentContext = resumedContext(),
  override val component: FlowComponent = ScopeFlowComponent(),
) : FlowNavigationComponent<TestConfig, TestChild>(context) {
  override val configSerializer: KSerializer<TestConfig> = TestConfig.serializer()

  override fun initialConfig(): List<TestConfig> = listOf(TestConfig.Root)

  override fun transition(event: Event): FlowTransition<TestConfig> {
    return when (event) {
      TestFlowEvent.DetailsRequested -> FlowTransition.NavigateTo(TestConfig.Details)
      TestFlowEvent.MoreRequested -> FlowTransition.NavigateTo(TestConfig.More)
      TestFlowEvent.BackRequested -> FlowTransition.Back
      TestFlowEvent.StayRequested -> FlowTransition.Stay
      else -> FlowTransition.Ignore
    }
  }

  override val childFactory: ChildScope.(TestConfig) -> TestChild = { TestChild(TestViewModel()) }

  fun back() = navigation.pop()

  fun activeViewModel(): TestViewModel = stack.value.active.instance.viewModel

  fun configs(): List<Any> = stack.value.items.map { it.configuration }
}

// Hosts TestFlowNavigationComponent as a nested flow
private class TestParentFlow : FlowNavigationComponent<TestParentConfig, TestParentChild>(resumedContext()) {
  override val component: FlowComponent = ScopeFlowComponent()

  override val configSerializer: KSerializer<TestParentConfig> = TestParentConfig.serializer()

  override fun initialConfig(): List<TestParentConfig> = listOf(TestParentConfig.Nested)

  override fun transition(event: Event): FlowTransition<TestParentConfig> {
    return when (event) {
      TestFlowEvent.ParentRequested,
      TestFlowEvent.StayRequested,
      -> FlowTransition.NavigateTo(TestParentConfig.Other)
      else -> FlowTransition.Ignore
    }
  }

  override val childFactory: ChildScope.(TestParentConfig) -> TestParentChild = { config ->
    when (config) {
      TestParentConfig.Nested -> TestParentChild.Nested(TestFlowNavigationComponent(context))
      TestParentConfig.Other -> TestParentChild.Other
    }
  }

  fun nested(): TestFlowNavigationComponent {
    return (
      stack.value.items
        .first()
        .instance as TestParentChild.Nested
    ).component
  }

  fun configs(): List<Any> = stack.value.items.map { it.configuration }
}

private class ScopeFlowComponent : FlowComponent {
  private val scope = CoroutineScope(Job())

  override fun viewModelProviders(): Set<ViewModelProvider> = emptySet()

  override fun assistedViewModelProviders(): Set<AssistedViewModelProvider> = emptySet()

  override fun flowViewModelProviders(): Set<ViewModelProvider> = emptySet()

  override fun assistedFlowViewModelProviders(): Set<AssistedViewModelProvider> = emptySet()

  override fun coroutineScope(): CoroutineScope = scope
}

@Serializable
private sealed interface TestConfig {
  @Serializable
  data object Root : TestConfig

  @Serializable
  data object Details : TestConfig

  @Serializable
  data object More : TestConfig
}

private class TestChild(override val viewModel: TestViewModel) : Screen

@Serializable
private sealed interface TestParentConfig {
  @Serializable
  data object Nested : TestParentConfig

  @Serializable
  data object Other : TestParentConfig
}

private sealed interface TestParentChild : Node {
  class Nested(override val component: TestFlowNavigationComponent) : TestParentChild, Flow
  data object Other : TestParentChild
}

private sealed interface TestFlowEvent : Event {
  data object DetailsRequested : TestFlowEvent
  data object MoreRequested : TestFlowEvent
  data object BackRequested : TestFlowEvent
  data object StayRequested : TestFlowEvent
  data object ParentRequested : TestFlowEvent
}

// Sends every event to its flow as is
private class TestViewModel : BaseViewModel<Unit, Nothing>() {
  // Runs intents in place, so an event reaches the flow before send returns
  override val container = viewModelScope.orbitContainer<Unit, Nothing>(
    initialState = Unit,
    buildSettings = { eventLoopDispatcher = { Dispatchers.Unconfined } },
  )

  fun send(event: Event) {
    intent { sendEvent(event) }
  }
}
