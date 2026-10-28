package com.kmpproject.core.routing.di

import com.tschuchort.compiletesting.KotlinCompilation
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class ViewModelBindingProcessorTest {
  private val appFlow = FlowTree.foregroundComponent().flow("appFlowComponent")

  @Test
  fun `registers view models in their flow under qualified class names`() {
    assertEquals(setOf(MAIN_VIEW_MODEL), appFlow.viewModelProviders().keys())
    assertEquals(setOf(DETAILS_VIEW_MODEL), appFlow.assistedViewModelProviders().keys())
  }

  @Test
  fun `creates new view model on each call`() {
    val first = appFlow.viewModel(MAIN_VIEW_MODEL)
    val second = appFlow.viewModel(MAIN_VIEW_MODEL)

    assertEquals(MAIN_VIEW_MODEL, first.javaClass.name)
    assertNotSame(first, second)
  }

  @Test
  fun `passes assisted params in constructor order and injects the rest from the flow graph`() {
    val main = appFlow.viewModel(MAIN_VIEW_MODEL)
    val details = appFlow.assistedViewModel(DETAILS_VIEW_MODEL, "Scooter", 42)

    assertEquals("Scooter", details.call("getTitle"))
    assertEquals(42, details.call("getId"))
    assertSame(main.call("getRepository"), details.call("getRepository"))
  }

  @Test
  fun `adds placeholder only for the kind of view models a flow lacks`() {
    val nestedFlow = appFlow.flow("nestedFlowComponent")
    val siblingFlow = appFlow.flow("siblingFlowComponent")

    assertEquals(listOf(MAIN_VIEW_MODEL), appFlow.viewModelProviders().map { it.key })
    assertEquals(listOf(DETAILS_VIEW_MODEL), appFlow.assistedViewModelProviders().map { it.key })
    assertEquals(listOf(NESTED_VIEW_MODEL), nestedFlow.viewModelProviders().map { it.key })
    assertEquals(listOf(PLACEHOLDER_KEY), nestedFlow.assistedViewModelProviders().map { it.key })
    assertEquals(listOf(PLACEHOLDER_KEY), siblingFlow.viewModelProviders().map { it.key })
    assertEquals(listOf(SIBLING_VIEW_MODEL), siblingFlow.assistedViewModelProviders().map { it.key })
  }

  @Test
  fun `registers view models of one scope from different modules`() {
    val feature = compile(appFlowSource, mainViewModelSource).assertCompiled()
    val root = compile(foregroundSource(), secondViewModelSource, classpath = feature.outputDirectory).assertCompiled()

    val appFlow = root.foregroundComponent().flow("appFlowComponent")

    assertEquals(setOf(MAIN_VIEW_MODEL, SECOND_VIEW_MODEL), appFlow.viewModelProviders().keys())
    assertEquals(MAIN_VIEW_MODEL, appFlow.viewModel(MAIN_VIEW_MODEL).javaClass.name)
    assertEquals(SECOND_VIEW_MODEL, appFlow.viewModel(SECOND_VIEW_MODEL).javaClass.name)
  }

  @Test
  fun `flow without view models does not compile`() {
    val result = compile(foregroundSource(), appFlowSource)

    assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
    assertContains(
      result.messages,
      "Cannot find an @Inject constructor or provider for: " +
        "@ScopedViewModel(value=AppFlowScope) Set<com.kmpproject.core.ui.viewmodel.ViewModelProvider>",
    )
  }
}
