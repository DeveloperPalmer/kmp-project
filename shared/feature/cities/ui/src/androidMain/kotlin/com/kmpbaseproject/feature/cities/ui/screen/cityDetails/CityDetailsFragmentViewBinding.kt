package com.kmpbaseproject.feature.cities.ui.screen.cityDetails

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
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
import com.kmpbaseproject.core.ui.viewmodel.ViewModelBinder
import com.kmpbaseproject.core.ui.viewmodel.ViewModelSlot
import com.kmpbaseproject.feature.cities.ui.views.R
import com.kmpbaseproject.feature.cities.ui.views.databinding.FragmentCityDetailsBinding
import com.kmpbaseproject.uikit.view.ViewAppTheme
import com.kmpbaseproject.uikit.view.applyPrimaryStyle
import com.kmpbaseproject.uikit.view.applyStyle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import com.kmpbaseproject.resources.R as ResourcesR

/**
 * The screen on Android Views with the same XML layout as [CityDetailsFragmentView], whose views come from the
 * generated FragmentCityDetailsBinding instead of findViewById
 */
class CityDetailsFragmentViewBinding :
  Fragment(R.layout.fragment_city_details),
  ViewModelBinder<CityDetailsViewModel> by ViewModelSlot() {
  // The binding of the current fragment view: created in onViewCreated, dropped in onDestroyView
  private var currentBinding: FragmentCityDetailsBinding? = null
  private val binding: FragmentCityDetailsBinding
    get() = checkNotNull(currentBinding) { "The binding exists only while the fragment has a view" }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // The fragment has inflated the layout already, so the binding only finds its views
    val binding = FragmentCityDetailsBinding.bind(view)
    currentBinding = binding
    applyTheme(binding)
    applyWindowInsets(binding.root)
    // Wired once per view, so the buttons work before the first state. The view model is read on click:
    // after Activity recreation the view exists before onBind hands the view model over
    binding.backButton.setOnClickListener {
      viewModel.value?.navigateBack()
    }
    binding.searchButton.setOnClickListener {
      viewModel.value?.searchCityInfo()
    }
    // Collects only while the view is at least started and stops with it, not with the fragment
    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.filterNotNull().collectLatest { model ->
          model.container.refCountStateFlow.collect(::render)
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    // The binding holds every view of the layout: kept in the back stack, it would keep the destroyed view tree
    currentBinding = null
  }

  private fun render(state: ViewState) {
    val city = state.city
    binding.cityInfo.isVisible = city != null
    if (city == null) return
    binding.cityValue.text = city.name
    binding.countryValue.text = city.country
    binding.populationValue.text = getString(
      ResourcesR.string.city_details_population_value,
      formatPopulation(city.population),
    )
  }

  // Views cannot read AppTheme, so the same design tokens come through ViewAppTheme. The theme is applied
  // to every new view: after switching to the dark theme the Activity and the fragment view are recreated
  private fun applyTheme(binding: FragmentCityDetailsBinding) {
    val theme = ViewAppTheme(requireContext())
    val colors = theme.colors
    val typography = theme.typography

    binding.root.setBackgroundColor(
      colors.background.primary.toArgb()
    )

    binding.backButton.imageTintList = ColorStateList.valueOf(
      colors.icon.primary.toArgb()
    )

    binding.title.applyStyle(
      typography.title4,
      colors.text.primary
    )
    for (label in listOf(binding.cityLabel, binding.countryLabel, binding.populationLabel)) {
      label.applyStyle(
        typography.subtitle1,
        colors.text.primary
      )
    }
    for (value in listOf(binding.cityValue, binding.countryValue, binding.populationValue)) {
      value.applyStyle(
        typography.body1,
        colors.text.primary
      )
    }
    binding.searchButton.applyPrimaryStyle(theme)
  }

  // The screen is edge-to-edge: the Compose UI pads itself with safeDrawingPadding, the views do it here
  private fun applyWindowInsets(root: View) {
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
      val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
      view.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = bars.bottom)
      insets
    }
  }
}
