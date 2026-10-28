package com.kmpproject.core.data.network

import com.kmpproject.core.domain.entity.ConnectivityError

fun interface PlatformErrorConverter {
  fun convert(error: Throwable): ConnectivityError?
}
