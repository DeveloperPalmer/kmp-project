package com.kmpproject.core.ui.viewmodel

import androidx.compose.runtime.Immutable
import com.kmpproject.core.ui.mvi.BaseViewModel

@Immutable
data class ViewModelProvider(
  val key: String,
  val factory: () -> BaseViewModel<*, *>,
)

// There is no other way to provide empty IntoSet dependencies (in Dagger we have @Multibinds for that),
// For now, empty FlowComponents with no VM-s (either assisted or not) have to provide placeholders:
// https://github.com/evant/kotlin-inject/issues/249
val emptyViewModelProvider = ViewModelProvider("empty") { error("should not be instantiated") }
val emptyAssistedViewModelProvider = AssistedViewModelProvider("empty") { error("should not be instantiated") }

@Immutable
data class AssistedViewModelProvider(
  val key: String,
  val factory: Factory,
) {
  fun interface Factory {
    fun build(vararg params: Any?): BaseViewModel<*, *>
  }
}
