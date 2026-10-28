package com.kmpproject.core.domain.entity

sealed interface LceState<out T : Any> {
  data object Loading : LceState<Nothing>
  data class Content<C : Any>(val value: C) : LceState<C>
  data class Error(val value: Throwable) : LceState<Nothing>
}
