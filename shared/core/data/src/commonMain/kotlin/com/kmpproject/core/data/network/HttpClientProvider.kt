package com.kmpproject.core.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

interface HttpClientProvider {
  fun create(config: HttpClientConfig<*>.() -> Unit): HttpClient
}
