package com.kmpproject.core.data.network

import com.kmpproject.core.domain.entity.ConnectivityError
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSURLErrorCannotConnectToHost
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorClientCertificateRejected
import platform.Foundation.NSURLErrorClientCertificateRequired
import platform.Foundation.NSURLErrorDNSLookupFailed
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorSecureConnectionFailed
import platform.Foundation.NSURLErrorServerCertificateHasBadDate
import platform.Foundation.NSURLErrorServerCertificateHasUnknownRoot
import platform.Foundation.NSURLErrorServerCertificateNotYetValid
import platform.Foundation.NSURLErrorServerCertificateUntrusted
import platform.Foundation.NSURLErrorTimedOut
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class IosPlatformErrorConverter : PlatformErrorConverter {
  override fun convert(error: Throwable): ConnectivityError? {
    if (error !is DarwinHttpRequestException || error.origin.domain != NSURLErrorDomain) {
      return null
    }
    return when (error.origin.code) {
      NSURLErrorTimedOut -> {
        ConnectivityError.TimeOut(cause = error)
      }
      NSURLErrorNotConnectedToInternet,
      NSURLErrorNetworkConnectionLost,
      NSURLErrorCannotFindHost,
      NSURLErrorCannotConnectToHost,
      NSURLErrorDNSLookupFailed -> {
        ConnectivityError.NoConnection(cause = error)
      }
      NSURLErrorSecureConnectionFailed,
      NSURLErrorServerCertificateHasBadDate,
      NSURLErrorServerCertificateUntrusted,
      NSURLErrorServerCertificateHasUnknownRoot,
      NSURLErrorServerCertificateNotYetValid,
      NSURLErrorClientCertificateRejected,
      NSURLErrorClientCertificateRequired -> {
        ConnectivityError.SSLError(cause = error)
      }
      else -> {
        null
      }
    }
  }
}
