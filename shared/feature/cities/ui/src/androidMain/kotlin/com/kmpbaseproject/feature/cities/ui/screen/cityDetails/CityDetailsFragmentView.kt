package com.kmpbaseproject.feature.cities.ui.screen.cityDetails

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kmpbaseproject.core.ui.formatPopulation
import com.kmpbaseproject.feature.cities.ui.views.R
import com.kmpbaseproject.uikit.view.ViewAppTheme
import com.kmpbaseproject.uikit.view.applyPrimaryStyle
import com.kmpbaseproject.uikit.view.applyStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import com.kmpbaseproject.resources.R as ResourcesR

/**
 * The screen on Android Views: an XML layout whose views are found with findViewById
 */
class CityDetailsFragmentView : Fragment(R.layout.fragment_city_details) {
  private val viewModel = MutableStateFlow<CityDetailsViewModel?>(null)
  private val boundViewModel: StateFlow<CityDetailsViewModel?> = viewModel

  // The views of the current fragment view: found in onViewCreated, dropped in onDestroyView
  private var views: Views? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val views = Views(view)
    this.views = views
    applyTheme(view)
    applyWindowInsets(view)
    // Wired once per view, so the buttons work before the first state. The view model is read on click:
    // after Activity recreation the view exists before onBind hands the view model over
    views.backButton.setOnClickListener {
      boundViewModel.value?.navigateBack()
    }
    views.searchButton.setOnClickListener {
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

  // AndroidFragment calls it once the fragment has reached onResume, so it comes after the view callbacks
  fun onBind(viewModel: CityDetailsViewModel) {
    this.viewModel.value = viewModel
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
    views.population.text = getString(
      ResourcesR.string.city_details_population_value,
      formatPopulation(city.population),
    )
  }

  // Views cannot read AppTheme, so the same design tokens come through ViewAppTheme. The theme is applied
  // to every new view: after switching to the dark theme the Activity and the fragment view are recreated
  private fun applyTheme(root: View) {
    val theme = ViewAppTheme(requireContext())
    val colors = theme.colors
    val typography = theme.typography

    root.setBackgroundColor(
      colors.background.primary.toArgb()
    )

    root.findViewById<ImageButton>(R.id.back_button).imageTintList = ColorStateList.valueOf(
      colors.icon.primary.toArgb()
    )

    root.findViewById<TextView>(R.id.title).applyStyle(
      typography.title4,
      colors.text.primary
    )
    for (label in listOf(R.id.city_label, R.id.country_label, R.id.population_label)) {
      root.findViewById<TextView>(label).applyStyle(
        typography.subtitle1,
        colors.text.primary
      )
    }
    for (value in listOf(R.id.city_value, R.id.country_value, R.id.population_value)) {
      root.findViewById<TextView>(value).applyStyle(
        typography.body1,
        colors.text.primary
      )
    }
    root.findViewById<Button>(R.id.search_button).applyPrimaryStyle(theme)
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
    val backButton: ImageButton = root.findViewById(R.id.back_button)
    val searchButton: Button = root.findViewById(R.id.search_button)
  }
}
