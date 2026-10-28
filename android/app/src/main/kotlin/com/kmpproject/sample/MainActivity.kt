package com.kmpproject.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commitNow
import com.arkivanov.decompose.defaultComponentContext
import com.kmpproject.core.component.AndroidAppComponent
import com.kmpproject.core.component.AppComponentHolder
import com.kmpproject.core.routing.flow.decompose.retainedFlowComponent
import com.kmpproject.feature.app.routing.decompose.AppFlow
import com.kmpproject.feature.app.routing.decompose.AppFlowNavigationComponent
import com.kmpproject.uikit.theme.AppTheme

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
    dropFragmentsAfterProcessDeath(savedInstanceState)
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

  // Fragments follow the stack: FragmentManager restores them after the process death too, but the screens that
  // hosted them start anew, and the fragments would wait for their containers until the Activity finishes
  private fun dropFragmentsAfterProcessDeath(savedInstanceState: Bundle?) {
    // A non-configuration instance is handed over only through recreation, without it the state outlived the process
    if (savedInstanceState == null || lastNonConfigurationInstance != null) return
    supportFragmentManager.commitNow {
      supportFragmentManager.fragments.forEach { fragment -> remove(fragment) }
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
