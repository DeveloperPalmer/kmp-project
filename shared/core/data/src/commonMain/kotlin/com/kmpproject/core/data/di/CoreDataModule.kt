package com.kmpproject.core.data.di

import com.kmpproject.core.data.network.HttpClientProvider
import com.kmpproject.core.data.network.PlatformErrorConverter
import com.kmpproject.core.data.network.installErrorResponseHandling
import com.kmpproject.core.domain.configuration.BuildConfiguration
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface CoreDataModule {
  @Provides
  @SingleIn(AppScope::class)
  fun provideHttpClient(
    provider: HttpClientProvider,
    platformErrorConverter: PlatformErrorConverter
  ): HttpClient {
    val json = Json { ignoreUnknownKeys = true }
    return provider.create {
      defaultRequest {
        url(BASE_URL)
      }
      install(Resources)
      install(ContentNegotiation) {
        json(json)
      }
      install(HttpTimeout) {
        connectTimeoutMillis = HTTP_TIMEOUT_MILLIS
        requestTimeoutMillis = HTTP_TIMEOUT_MILLIS
        socketTimeoutMillis = HTTP_TIMEOUT_MILLIS
      }
      installErrorResponseHandling(
        json = json,
        platformErrorConverter = platformErrorConverter
      )
    }
  }
}

internal val BuildConfiguration.httpLogLevel: LogLevel
  get() = when (this) {
    is BuildConfiguration.Dev,
    is BuildConfiguration.Internal -> LogLevel.ALL
    is BuildConfiguration.Production -> LogLevel.NONE
  }

private const val BASE_URL = "http://dev-dep.tools.urent.tech:8080/api/"
private const val HTTP_TIMEOUT_MILLIS = 60_000L
