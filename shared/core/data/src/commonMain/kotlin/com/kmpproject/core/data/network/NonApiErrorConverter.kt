package com.kmpproject.core.data.network

import com.kmpproject.core.domain.entity.ConnectivityError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException

internal fun convertNonApiError(
  error: Throwable,
  platformErrorConverter: PlatformErrorConverter,
): ConnectivityError? {
  return when (error) {
    is HttpRequestTimeoutException,
    is ConnectTimeoutException,
    is SocketTimeoutException -> ConnectivityError.TimeOut(cause = error)
    else -> platformErrorConverter.convert(error = error)
  }
}
