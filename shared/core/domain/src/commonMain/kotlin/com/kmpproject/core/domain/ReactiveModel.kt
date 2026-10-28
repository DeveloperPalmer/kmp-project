package com.kmpproject.core.domain

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

open class ReactiveModel(parentScope: CoroutineScope) : ru.kode.remo.ReactiveModel(parentScope) {
  init {
    uncaughtExceptions
      .onEach { e -> e.printStackTrace() }
      .launchIn(scope)
  }
}

fun createCoroutineScope(name: String): CoroutineScope {
  val errorHandler = CoroutineExceptionHandler { _, e ->
    e.printStackTrace()
  }
  return CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName(name) + errorHandler)
}
