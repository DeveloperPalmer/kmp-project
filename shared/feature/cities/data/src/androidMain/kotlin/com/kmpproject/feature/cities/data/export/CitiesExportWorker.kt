package com.kmpproject.feature.cities.data.export

import android.R
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.kmpproject.core.domain.storage.DownloadsStorage
import com.kmpproject.feature.cities.domain.CitiesRepository
import com.kmpproject.resources.Res
import com.kmpproject.resources.cities_export_cancel
import com.kmpproject.resources.cities_export_channel_name
import com.kmpproject.resources.cities_export_notification_title
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

class CitiesExportWorker @Inject constructor(
  @Assisted
  context: Context,
  @Assisted
  params: WorkerParameters,
  private val citiesRepository: CitiesRepository,
  private val downloadsStorage: DownloadsStorage,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    val query = inputData.getString(KEY_QUERY) ?: return Result.failure()
    val isForeground = tryStartForeground()
    val cities = citiesRepository.cities(query)
    val markdown = cities
      .mapIndexed { index, city ->
        delay(500.milliseconds)
        // Same notification id, so the foreground notification is updated in place
        if (isForeground) {
          setForeground(foregroundInfo(progress = (index + 1f) / cities.size))
        }
        "${index + 1}. ${city.title}"
      }
      .joinToString(
        separator = "\n",
        postfix = "\n"
      )
    downloadsStorage.save(
      fileName = FILE_NAME,
      mimeType = MIME_TYPE,
      content = markdown
    )
    return Result.success()
  }

  // Since S a foreground service can't start from the background, which is where periodic runs
  // usually happen; such a run continues as regular work without the notification
  private suspend fun tryStartForeground(): Boolean {
    return try {
      setForeground(getForegroundInfo())
      true
    } catch (e: CancellationException) {
      // CancellationException is an IllegalStateException, so it has to be let through first
      throw e
    } catch (_: IllegalStateException) {
      false
    }
  }

  override suspend fun getForegroundInfo(): ForegroundInfo {
    return foregroundInfo(progress = 0f)
  }

  private suspend fun foregroundInfo(progress: Float): ForegroundInfo {
    val channel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
      .setName(getString(Res.string.cities_export_channel_name))
      .build()
    // Creating an existing channel is a no-op
    NotificationManagerCompat
      .from(applicationContext)
      .createNotificationChannel(channel)
    // Cancels this work: the coroutine is cancelled and the file is never written
    val cancelIntent = WorkManager
      .getInstance(applicationContext)
      .createCancelPendingIntent(id)
    val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
      .setSmallIcon(R.drawable.stat_sys_download)
      .setContentTitle(getString(Res.string.cities_export_notification_title))
      .setProgress(PROGRESS_MAX, (progress * PROGRESS_MAX).roundToInt(), false)
      .setOnlyAlertOnce(true)
      .setOngoing(true)
      .addAction(R.drawable.ic_menu_close_clear_cancel, getString(Res.string.cities_export_cancel), cancelIntent)
      // Since S the system otherwise delays foreground service notifications by up to 10 seconds
      .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
      .build()
    // Foreground service types exist since Q
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ForegroundInfo(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    } else {
      ForegroundInfo(NOTIFICATION_ID, notification)
    }
  }

  companion object {
    const val KEY_QUERY = "query"
  }
}

private const val FILE_NAME = "cities.md"
private const val MIME_TYPE = "text/markdown"
private const val CHANNEL_ID = "cities_export"
private const val NOTIFICATION_ID = 1
private const val PROGRESS_MAX = 100
