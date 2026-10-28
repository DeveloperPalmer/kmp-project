package com.kmpproject.core.routing.di

import com.tschuchort.compiletesting.KotlinCompilation
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class ChildComponentFactoryProcessorTest {
  private val foreground = FlowTree.foregroundComponent()
  private val appFlow = foreground.flow("appFlowComponent")
  private val nestedFlow = appFlow.flow("nestedFlowComponent")
  private val siblingFlow = appFlow.flow("siblingFlowComponent")

  @Test
  fun `nested flow sees view models of parent flows`() {
    assertEquals(setOf(MAIN_VIEW_MODEL, NESTED_VIEW_MODEL), nestedFlow.flowViewModelProviders().keys())
    assertEquals(setOf(DETAILS_VIEW_MODEL), nestedFlow.assistedFlowViewModelProviders().keys())
    assertEquals(MAIN_VIEW_MODEL, nestedFlow.viewModel(MAIN_VIEW_MODEL).javaClass.name)
  }

  @Test
  fun `parent and sibling flows do not see view models of a nested flow`() {
    assertEquals(setOf(MAIN_VIEW_MODEL), appFlow.flowViewModelProviders().keys())
    assertEquals(setOf(DETAILS_VIEW_MODEL), appFlow.assistedFlowViewModelProviders().keys())
    assertEquals(setOf(MAIN_VIEW_MODEL), siblingFlow.flowViewModelProviders().keys())
    assertEquals(setOf(DETAILS_VIEW_MODEL, SIBLING_VIEW_MODEL), siblingFlow.assistedFlowViewModelProviders().keys())
  }

  @Test
  fun `nested flow shares scoped dependencies of parent flow`() {
    val mainRepository = appFlow.viewModel(MAIN_VIEW_MODEL).call("getRepository")
    val nestedRepository = nestedFlow.viewModel(NESTED_VIEW_MODEL).call("getRepository")

    assertSame(mainRepository, nestedRepository)
  }

  @Test
  fun `creates independent flow graph on each accessor call`() {
    val otherAppFlow = foreground.flow("appFlowComponent")

    assertNotSame(appFlow, otherAppFlow)
    assertNotSame(nestedFlow, appFlow.flow("nestedFlowComponent"))
    assertNotSame(
      appFlow.viewModel(MAIN_VIEW_MODEL).call("getRepository"),
      otherAppFlow.viewModel(MAIN_VIEW_MODEL).call("getRepository"),
    )
  }

  // Why Android/IosForegroundComponent redeclare appFlowComponent(): the processor reads only declared members
  @Test
  fun `accessor of child flow inherited without redeclaration is not bound`() {
    val result = compile(foregroundSource(redeclareAccessor = false), appFlowSource, mainViewModelSource)

    assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
    assertContains(result.messages, "Cannot find an @Inject constructor or provider for: fixture.AppFlowComponent")
  }
}
