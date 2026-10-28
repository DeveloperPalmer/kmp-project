package com.kmpproject.core.routing.di

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class FlowComponentBindingProcessorTest {
  private val foreground = FlowTree.foregroundComponent()
  private val appFlow = foreground.flow("appFlowComponent")

  @Test
  fun `creates one coroutine scope per flow component`() {
    val otherAppFlow = foreground.flow("appFlowComponent")

    assertSame(appFlow.coroutineScope(), appFlow.coroutineScope())
    assertNotSame(appFlow.coroutineScope(), otherAppFlow.coroutineScope())
  }

  @Test
  fun `names coroutine scope after flow scope and installs exception handler`() {
    val context = appFlow.coroutineScope().coroutineContext

    assertEquals("AppFlowScope", context[CoroutineName]?.name)
    assertNotNull(context[CoroutineExceptionHandler])
  }
}
