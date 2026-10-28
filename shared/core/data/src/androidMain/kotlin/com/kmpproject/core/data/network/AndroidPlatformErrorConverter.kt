package com.kmpproject.core.data.network

import com.kmpproject.core.domain.entity.ConnectivityError
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import java.net.ConnectException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

@Inject
@ContributesBinding(AppScope::class)
class AndroidPlatformErrorConverter : PlatformErrorConverter {
  override fun convert(error: Throwable): ConnectivityError? {
    return when (error) {
      is SSLException -> ConnectivityError.SSLError(cause = error)
      is ConnectException -> ConnectivityError.NoConnection(cause = error)
      is UnknownHostException -> ConnectivityError.NoConnection(cause = error)
      else -> null
    }
  }
}
