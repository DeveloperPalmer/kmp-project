package com.kmpproject.core.data.storage

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.kmpproject.core.domain.ApplicationContext
import com.kmpproject.core.domain.storage.DownloadsStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import java.io.File
import java.io.IOException

@Inject
@ContributesBinding(AppScope::class)
class AndroidDownloadsStorage(
  @ApplicationContext
  private val context: Context,
) : DownloadsStorage {
  override suspend fun save(fileName: String, mimeType: String, content: String) {
    withContext(Dispatchers.IO) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        saveToMediaStore(fileName, mimeType, content)
      } else {
        saveToAppDownloads(fileName, content)
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.Q)
  private fun saveToMediaStore(fileName: String, mimeType: String, content: String) {
    val resolver = context.contentResolver
    val pending = ContentValues().apply {
      put(MediaStore.Downloads.DISPLAY_NAME, fileName)
      put(MediaStore.Downloads.MIME_TYPE, mimeType)
      put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
      // Hidden from other apps until fully written
      put(MediaStore.Downloads.IS_PENDING, 1)
    }
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, pending)
      ?: error("MediaStore refused to create $fileName")
    try {
      resolver.openOutputStream(uri)
        ?.use { it.write(content.toByteArray()) }
        ?: throw IOException("No output stream for $uri")
      val published = ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }
      resolver.update(uri, published, null, null)
    } catch (e: IOException) {
      resolver.delete(uri, null, null)
      throw e
    }
  }

  // Public Downloads needs WRITE_EXTERNAL_STORAGE before Q, the app-specific one needs nothing
  private fun saveToAppDownloads(fileName: String, content: String) {
    val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: error(
      "External storage is unavailable"
    )
    File(directory, fileName).writeText(content)
  }
}
