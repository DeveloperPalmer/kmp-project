package com.kmpproject.core.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.kmpproject.core.domain.AppLauncher
import com.kmpproject.core.domain.ApplicationContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@Inject
@ContributesBinding(AppScope::class)
class AndroidAppLauncher(
  @ApplicationContext
  private val context: Context,
) : AppLauncher {
  override fun openWebsite(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
  }
}
