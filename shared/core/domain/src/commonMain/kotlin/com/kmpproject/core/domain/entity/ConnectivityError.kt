package com.kmpproject.core.domain.entity

sealed class ConnectivityError(cause: Throwable?) : RuntimeException(cause) {
  data class TimeOut(
    override val cause: Throwable,
  ) : ConnectivityError(cause)

  data class NoConnection(
    override val cause: Throwable?,
  ) : ConnectivityError(cause)

  data class SSLError(
    override val cause: Throwable?,
  ) : ConnectivityError(cause)
}
