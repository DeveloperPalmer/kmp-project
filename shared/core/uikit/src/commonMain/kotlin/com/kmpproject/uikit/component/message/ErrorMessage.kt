package com.kmpproject.uikit.component.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.kmpproject.core.ui.entity.UiMessage
import com.kmpproject.resources.Res
import com.kmpproject.resources.common_error_description
import com.kmpproject.resources.common_error_title
import com.kmpproject.resources.error_no_connection_description
import com.kmpproject.resources.error_no_connection_title
import com.kmpproject.resources.img_no_connection_88
import com.kmpproject.uikit.theme.AppTheme
import com.kmpproject.uikit.theme.VSpacer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ErrorMessage(
  message: UiMessage,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.padding(horizontal = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    message.illustration?.let { illustration ->
      Image(
        modifier = Modifier.size(88.dp),
        painter = painterResource(illustration),
        contentDescription = null,
      )
      VSpacer(24.dp)
    }
    Text(
      text = stringResource(message.title),
      style = AppTheme.typography.title2,
      color = AppTheme.colors.text.primary,
      textAlign = TextAlign.Center,
    )
    message.description?.let { description ->
      VSpacer(16.dp)
      Text(
        text = stringResource(description),
        style = AppTheme.typography.body1,
        color = AppTheme.colors.text.secondary,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Preview
@Composable
private fun ErrorMessagePreviewLight(
  @PreviewParameter(ErrorMessagePreviewProvider::class)
  message: UiMessage,
) {
  AppTheme(useDarkTheme = false) {
    ErrorMessagePreviewContent(message)
  }
}

@Preview
@Composable
private fun ErrorMessagePreviewDark(
  @PreviewParameter(ErrorMessagePreviewProvider::class)
  message: UiMessage,
) {
  AppTheme(useDarkTheme = true) {
    ErrorMessagePreviewContent(message)
  }
}

@Composable
private fun ErrorMessagePreviewContent(message: UiMessage) {
  ErrorMessage(
    modifier = Modifier
      .background(AppTheme.colors.background.primary)
      .padding(vertical = 16.dp),
    message = message,
  )
}

private class ErrorMessagePreviewProvider : PreviewParameterProvider<UiMessage> {
  override val values = sequenceOf(
    UiMessage(
      title = Res.string.error_no_connection_title,
      description = Res.string.error_no_connection_description,
      illustration = Res.drawable.img_no_connection_88,
    ),
    UiMessage(
      title = Res.string.common_error_title,
      description = Res.string.common_error_description,
    ),
  )
}
