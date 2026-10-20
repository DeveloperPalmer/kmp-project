package com.kmpbaseproject.feature.cities.ui.screen.cityDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.kmpbaseproject.uikit.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * A fragment around the shared Compose screen: the fragment brings its lifecycle, the UI stays the same
 */
class CityDetailsFragmentCompose : Fragment() {
  private val viewModel = MutableStateFlow<CityDetailsViewModel?>(null)
  private val boundViewModel: StateFlow<CityDetailsViewModel?> = viewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return ComposeView(requireContext()).apply {
      // The composition lives as long as the fragment's view: in the back stack the view is destroyed,
      // the fragment is not
      setViewCompositionStrategy(
        ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
      )
      setContent {
        val viewModel by boundViewModel.collectAsState()
        AppTheme {
          // The Compose UI itself, not CityDetailsScreen: on Android that one would host this fragment again
          viewModel?.let { CityDetailsComposeScreen(viewModel = it) }
        }
      }
    }
  }

  // AndroidFragment calls it once the fragment has reached onResume, so it comes after the view callbacks
  fun onBind(viewModel: CityDetailsViewModel) {
    this.viewModel.value = viewModel
  }
}
