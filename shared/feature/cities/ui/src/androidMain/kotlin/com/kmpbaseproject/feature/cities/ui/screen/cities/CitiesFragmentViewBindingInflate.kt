package com.kmpbaseproject.feature.cities.ui.screen.cities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kmpbaseproject.core.domain.mapDistinctChanges
import com.kmpbaseproject.core.ui.viewmodel.ViewModelBinder
import com.kmpbaseproject.core.ui.viewmodel.ViewModelSlot
import com.kmpbaseproject.feature.cities.ui.screen.cities.recycler.CitiesAdapter
import com.kmpbaseproject.feature.cities.ui.screen.cities.recycler.CityDividerDecoration
import com.kmpbaseproject.feature.cities.ui.screen.cities.recycler.CityViewHolder
import com.kmpbaseproject.feature.cities.ui.views.databinding.FragmentCitiesBinding
import com.kmpbaseproject.resources.R
import com.kmpbaseproject.uikit.view.ViewAppTheme
import com.kmpbaseproject.uikit.view.applyStyle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/**
 * Список городов на Android View: фрагмент создаёт разметку через ViewBinding в onCreateView, а города показывает
 * RecyclerView.
 *
 * RecyclerView собирается из трёх частей:
 * - LayoutManager расставляет строки и решает, какие из них видны: вертикальный список, сетка, горизонтальная лента;
 * - Adapter даёт данные: сколько строк, как создать строку и как заполнить её — здесь [CitiesAdapter];
 * - ViewHolder держит View одной строки, и RecyclerView переиспользует его для других данных — [CityViewHolder].
 *
 * А ItemDecoration рисует между строками то, что не входит в их разметку, — здесь [CityDividerDecoration]
 */
class CitiesFragmentViewBindingInflate :
  Fragment(),
  ViewModelBinder<CitiesViewModel> by ViewModelSlot() {
  // Binding текущей View фрагмента: создаётся в onCreateView, обнуляется в onDestroyView
  private var currentBinding: FragmentCitiesBinding? = null
  private val binding: FragmentCitiesBinding
    get() = checkNotNull(currentBinding) { "The binding exists only while the fragment has a view" }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    currentBinding = FragmentCitiesBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val theme = ViewAppTheme(requireContext())
    applyTheme(theme)
    applyWindowInsets(binding.root)
    // Адаптер живёт столько же, сколько View, поэтому он локальный, а не поле фрагмента. RecyclerView подписывается
    // на адаптер, и адаптер в поле держал бы через эту подписку уничтоженный RecyclerView всё время, пока фрагмент
    // без View лежит в back stack. Локальная переменная нужна и сборщику ниже: свойство RecyclerView.adapter имеет
    // тип RecyclerView.Adapter<*>, и submitData у него нет. Город читается в момент нажатия: ViewModel могла прийти
    // позже View
    val citiesAdapter = CitiesAdapter(
      theme = theme,
      onCityClick = { viewModel.value?.openDetails(it.id) }
    )
    binding.citiesList.apply {
      adapter = citiesAdapter
      val itemDecoration = CityDividerDecoration(
        color = theme.colors.divider.primary.toArgb(),
        thickness = resources.getDimensionPixelSize(R.dimen.cities_divider_thickness),
      )
      addItemDecoration(itemDecoration)
    }

    // Сбор идёт, только пока View хотя бы в STARTED. PagingData закешированы в CitiesModel через cachedIn,
    // поэтому повторный сбор после возврата на экран не загружает список заново
    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.filterNotNull().collectLatest { model ->
          model.container.refCountStateFlow
            .mapDistinctChanges { state -> state.cities }
            .collectLatest {
              it.collectLatest { pagingCity ->
                // submitData приостанавливается, пока эти PagingData актуальны, поэтому collectLatest: новые PagingData
                // после нового поиска отменяют показ старых
                citiesAdapter.submitData(pagingCity)
              }
            }
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    // Binding держит все View разметки: в back stack он удерживал бы уже уничтоженное дерево View
    currentBinding = null
  }

  // View не умеют читать AppTheme, поэтому те же дизайн-токены приходят через ViewAppTheme
  private fun applyTheme(theme: ViewAppTheme) {
    binding.root.setBackgroundColor(theme.colors.background.primary.toArgb())
    binding.title.applyStyle(theme.typography.title4, theme.colors.text.primary)
  }

  // Экран рисуется edge-to-edge: Compose UI отступает от системных панелей сам через safeDrawingPadding, а View здесь
  private fun applyWindowInsets(root: View) {
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
      val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
      view.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = bars.bottom)
      insets
    }
  }
}
