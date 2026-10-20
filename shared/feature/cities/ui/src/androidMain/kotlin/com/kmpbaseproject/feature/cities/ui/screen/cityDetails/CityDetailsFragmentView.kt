package com.kmpbaseproject.feature.cities.ui.screen.cityDetails

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kmpbaseproject.core.ui.formatPopulation
import com.kmpbaseproject.feature.cities.ui.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * The screen on Android Views: an XML layout whose views are found with findViewById
 */
class CityDetailsFragmentView : Fragment(R.layout.fragment_city_details) {
  private val viewModel = MutableStateFlow<CityDetailsViewModel?>(null)
  private val boundViewModel: StateFlow<CityDetailsViewModel?> = viewModel

  // The views of the current fragment view: found in onViewCreated, dropped in onDestroyView
  private var views: Views? = null

  fun onBind(viewModel: CityDetailsViewModel) {
    this.viewModel.value = viewModel
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    views = Views(view)
    applyWindowInsets(view)
    view.findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
      boundViewModel.value?.navigateBack()
    }
    view.findViewById<Button>(R.id.search_button).setOnClickListener {
      boundViewModel.value?.searchCityInfo()
    }
    // Collects only while the view is at least started and stops with it, not with the fragment
    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        boundViewModel.filterNotNull().collectLatest { model ->
          model.container.refCountStateFlow.collect(::render)
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    // The fragment outlives its view in the back stack: holding the views would keep the destroyed view tree
    views = null
  }

  private fun render(state: ViewState) {
    val views = views ?: return
    val city = state.city
    views.cityInfo.isVisible = city != null
    if (city == null) return
    views.city.text = city.name
    views.country.text = city.country
    views.population.text = getString(R.string.city_details_population_value, formatPopulation(city.population))
  }

  // The screen is edge-to-edge: the Compose UI pads itself with safeDrawingPadding, the views do it here
  private fun applyWindowInsets(root: View) {
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
      val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
      view.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = bars.bottom)
      insets
    }
  }

  private class Views(root: View) {
    val cityInfo: View = root.findViewById(R.id.city_info)
    val city: TextView = root.findViewById(R.id.city_value)
    val country: TextView = root.findViewById(R.id.country_value)
    val population: TextView = root.findViewById(R.id.population_value)
  }
}
