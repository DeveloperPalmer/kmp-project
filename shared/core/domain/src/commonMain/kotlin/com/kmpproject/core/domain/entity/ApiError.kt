package com.kmpproject.core.domain.entity

data class ApiError(
  val code: Int,
  val description: String?,
) : RuntimeException(description)
