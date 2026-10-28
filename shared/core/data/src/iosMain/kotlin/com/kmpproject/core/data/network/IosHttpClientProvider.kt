package com.kmpproject.core.data.network

import com.kmpproject.core.data.di.httpLogLevel
import com.kmpproject.core.domain.configuration.BuildConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class IosHttpClientProvider(
  private val buildConfiguration: BuildConfiguration,
) : HttpClientProvider {
  override fun create(config: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Darwin) {
      install(Logging) {
        logger = Logger.DEFAULT
        level = buildConfiguration.httpLogLevel
      }
      config()
    }
  }
}
