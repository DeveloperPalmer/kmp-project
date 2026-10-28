package com.kmpproject.core.data.network

import com.kmpproject.core.domain.entity.ApiError
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

internal fun HttpClientConfig<*>.installErrorResponseHandling(
  json: Json,
  platformErrorConverter: PlatformErrorConverter,
) {
  expectSuccess = true

  HttpResponseValidator {
    handleResponseExceptionWithRequest { exception, _ ->
      val error = when (exception) {
        is ResponseException -> exception.toApiError(json)
        else -> convertNonApiError(exception, platformErrorConverter)
      }
      if (error != null) {
        throw error
      }
    }
  }
}

private suspend fun ResponseException.toApiError(json: Json): ApiError {
  val description = try {
    json.decodeFromString<ErrorModel>(response.bodyAsText()).error
  } catch (_: SerializationException) {
    null
  }
  return ApiError(
    code = response.status.value,
    description = description
  )
}
