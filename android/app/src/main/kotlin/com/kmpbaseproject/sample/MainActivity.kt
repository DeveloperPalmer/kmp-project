package com.kmpbaseproject.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.arkivanov.decompose.defaultComponentContext
import com.kmpbaseproject.core.component.AndroidAppComponent
import com.kmpbaseproject.core.component.AppComponentHolder
import com.kmpbaseproject.core.routing.flow.decompose.retainedFlowComponent
import com.kmpbaseproject.feature.app.routing.decompose.AppFlow
import com.kmpbaseproject.feature.app.routing.decompose.AppFlowNavigationComponent
import com.kmpbaseproject.uikit.theme.AppTheme

// A FragmentActivity, so that screens can host fragments through AndroidFragment
class MainActivity : FragmentActivity() {
  // Without the permission foreground work still runs, only its notification is hidden
  private val notificationPermissionRequest = registerForActivityResult(RequestPermission()) {}

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    val appComponent = (applicationContext as AppComponentHolder).appComponent as AndroidAppComponent
    // The stack is kept only through Activity recreation: after the process death the data of the screens is gone,
    // so the app starts anew instead of restoring screens that have nothing to show
    val context = defaultComponentContext(
      isStateSavingAllowed = { isChangingConfigurations }
    )
    // The DI graph of the root flow is kept with the view models that use it and dies when the Activity finishes
    val appFlowNavigationComponent = AppFlowNavigationComponent(
      context = context,
      component = context.retainedFlowComponent {
        appComponent.foregroundComponentFactory()
          .create()
          .appFlowComponent()
      },
    )
    requestNotificationPermission()
    setContent {
      AppTheme {
        AppFlow(
          component = appFlowNavigationComponent
        )
      }
    }
  }

  private fun requestNotificationPermission() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val permission = Manifest.permission.POST_NOTIFICATIONS
    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
      notificationPermissionRequest.launch(permission)
    }
  }
}
