package com.kmpbaseproject.feature.cities.ui.screen.cityDetails

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.kmpbaseproject.feature.cities.ui.views.databinding.FragmentCityDetailsBinding
import com.kmpbaseproject.uikit.view.ViewAppTheme
import com.kmpbaseproject.uikit.view.applyPrimaryStyle
import com.kmpbaseproject.uikit.view.applyStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

// Каждый колбэк жизненного цикла фрагмента ниже пишет себя в лог: `adb logcat -s CityDetailsLifecycle` покажет
// их порядок, пока экран открывается, поворачивается, уходит в фон и закрывается
private const val LIFECYCLE_TAG = "CityDetailsLifecycle"

/**
 * Экран на ViewBinding, как [CityDetailsFragmentViewBinding], но разметку здесь создаёт сам binding в onCreateView.
 * Там layout передан в конструктор Fragment(R.layout.…): разметку создаёт onCreateView фрагмента по умолчанию
 * тем же вызовом inflater.inflate(layoutId, container, false), а binding через bind только находит в ней View.
 *
 * Заодно здесь переопределены все колбэки жизненного цикла в порядке вызова, с пояснением, зачем каждый.
 * У фрагмента два жизненных цикла: свой, от onAttach до onDetach, и более короткий у его View (viewLifecycleOwner),
 * от onCreateView до onDestroyView. В back stack фрагмент живёт без View, поэтому всё, что трогает View,
 * привязывается к жизненному циклу View.
 *
 * Порядок вызовов, снятый с эмулятора:
 * - открытие: onAttach → onCreate → onCreateView → onViewCreated → onViewStateRestored → onStart → onResume → onBind;
 * - кнопка Home: onPause → onStop → onSaveInstanceState; возврат в приложение: onStart → onResume;
 * - поворот: onPause → onStop → onSaveInstanceState дважды → onDestroyView → onDestroy → onDetach, затем новый
 *   экземпляр с тем же who проходит весь путь открытия;
 * - «назад»: onSaveInstanceState → onPause → onStop → onDestroyView → onDestroy → onDetach;
 * - возврат после смерти процесса: onAttach → onCreate → onDestroy → onDetach, восстановленный фрагмент сразу
 *   удаляет MainActivity, потому что стек экранов начинается заново
 */
class CityDetailsFragmentViewBindingInflate : Fragment() {
  // У класса намеренно нет параметров конструктора: FragmentManager пересоздаёт фрагмент по имени класса через пустой
  // конструктор, после пересоздания Activity и после смерти процесса. Поэтому через конструктор ничего не передаётся:
  // аргументы кладут в Bundle (arguments), а ViewModel приходит через onBind
  private val viewModel = MutableStateFlow<CityDetailsViewModel?>(null)
  private val boundViewModel: StateFlow<CityDetailsViewModel?> = viewModel

  // Binding текущей View фрагмента: создаётся в onCreateView, обнуляется в onDestroyView
  private var currentBinding: FragmentCityDetailsBinding? = null
  private val binding: FragmentCityDetailsBinding
    get() = checkNotNull(currentBinding) { "The binding exists only while the fragment has a view" }

  // Вызывает AndroidFragment, получив фрагмент. Новый фрагмент к этому моменту уже добавлен и прошёл весь путь
  // до onResume, пересозданный найден по id контейнера. Поэтому View ждут ViewModel в StateFlow
  fun onBind(viewModel: CityDetailsViewModel) {
    logLifecycle("onBind")
    this.viewModel.value = viewModel
  }

  // Фрагмент прикреплён к хосту, то есть к Activity и её FragmentManager: с этого момента работают requireContext()
  // и requireActivity()
  override fun onAttach(context: Context) {
    super.onAttach(context)
    logLifecycle("onAttach")
  }

  // Фрагмент создан, View у него ещё нет: место для настройки, которая живёт столько же, сколько фрагмент,
  // и не трогает View. savedInstanceState не null, когда FragmentManager пересоздаёт фрагмент
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    logLifecycle("onCreate")
  }

  // Создаёт View фрагмента и больше ничего: настраивают View в onViewCreated
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    logLifecycle("onCreateView")
    // inflate превращает XML-разметку в объекты View: на каждый тег создаётся объект его класса, настроенный
    // атрибутами тега, а вложенные теги становятся дочерними View. inflate у binding создаёт разметку и сразу
    // находит в ней View с id, как это делает bind.
    // Контейнер нужен ради атрибутов layout_* у корня разметки: они описывают, как View встанет в родителе,
    // и прочитать их может только родитель. С null вместо контейнера корень потерял бы размеры и отступы из XML.
    // attachToParent = false: контейнер нужен только чтобы прочитать эти атрибуты, а саму View в него добавит
    // FragmentManager. Если добавить её и здесь, у View окажется второй родитель, и приложение упадёт.
    // В onCreateViewHolder у RecyclerView по той же причине пишут false
    currentBinding = FragmentCityDetailsBinding.inflate(inflater, container, false)
    return binding.root
  }

  // View создана: здесь настраивают View, вешают слушателей и подписываются на состояние.
  // Устаревший onActivityCreated шёл сразу после него, его работа теперь тоже здесь
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    logLifecycle("onViewCreated")
    applyTheme(binding)
    applyWindowInsets(binding.root)
    // Слушатели вешаются один раз на каждую View, чтобы кнопки работали ещё до первого состояния. ViewModel
    // читается в момент нажатия: после пересоздания Activity View появляется раньше, чем onBind передаст ViewModel
    binding.backButton.setOnClickListener {
      boundViewModel.value?.navigateBack()
    }
    binding.searchButton.setOnClickListener {
      boundViewModel.value?.searchCityInfo()
    }
    // Сбор идёт, только пока View хотя бы в STARTED, и останавливается вместе с View, а не с фрагментом
    viewLifecycleOwner.lifecycleScope.launch {
      viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        boundViewModel.filterNotNull().collectLatest { model ->
          model.container.refCountStateFlow.collect(::render)
        }
      }
    }
  }

  // View вернули своё сохранённое состояние: текст в EditText, отметку чекбокса, позицию прокрутки.
  // Восстанавливается оно у View с id. Для нового фрагмента сюда приходит null
  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)
    logLifecycle("onViewStateRestored")
  }

  // Фрагмент виден. View тоже переходит в STARTED, и repeatOnLifecycle из onViewCreated начинает собирать состояние
  override fun onStart() {
    super.onStart()
    logLifecycle("onStart")
  }

  // Фрагмент на переднем плане и принимает ввод
  override fun onResume() {
    super.onResume()
    logLifecycle("onResume")
  }

  // Фрагмент уходит с переднего плана: приложение уходит в фон, поверх открывается другая Activity или фрагмент
  // удаляют. При этом он может оставаться видимым, например в режиме нескольких окон
  override fun onPause() {
    super.onPause()
    logLifecycle("onPause")
  }

  // Фрагмент больше не виден. repeatOnLifecycle отменяет сбор: скрытый экран ничего не отрисовывает
  override fun onStop() {
    super.onStop()
    logLifecycle("onStop")
  }

  // Сохраняет небольшое состояние UI в Bundle, который переживает пересоздание Activity и смерть процесса.
  // С Android 9 приходит после onStop. FragmentManager вызывает его, когда сохраняет состояние Activity: при уходе
  // в фон и перед пересозданием. Здесь его вызывает ещё и AndroidFragment, когда уходит из композиции: он сохраняет
  // состояние фрагмента у себя. Отсюда два вызова при повороте и вызов перед удалением по «назад».
  // Сохранять тут нечего: состояние экрана живёт во ViewModel, а View сохраняют своё состояние сами
  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    logLifecycle("onSaveInstanceState")
  }

  // View уничтожена, а фрагмент ещё нет: он либо ждёт в back stack новую View, либо следом уничтожается.
  // Корутины viewLifecycleOwner к этому моменту отменены
  override fun onDestroyView() {
    super.onDestroyView()
    logLifecycle("onDestroyView")
    // Binding держит все View разметки: в back stack он удерживал бы уже уничтоженное дерево View
    currentBinding = null
  }

  // Фрагмент уничтожен: его удалили насовсем или уничтожается его Activity. При пересоздании Activity
  // FragmentManager создаёт новый экземпляр с тем же who и сохранённым состоянием
  override fun onDestroy() {
    super.onDestroy()
    logLifecycle("onDestroy")
  }

  // Фрагмент откреплён от хоста: контекста у него больше нет
  override fun onDetach() {
    super.onDetach()
    logLifecycle("onDetach")
  }

  // toString фрагмента содержит хэш экземпляра и who, внутренний id фрагмента в FragmentManager. Пересозданный
  // фрагмент — это новый объект с тем же who
  private fun logLifecycle(event: String) {
    Log.d(LIFECYCLE_TAG, "$event: $this")
  }

  private fun render(state: ViewState) {
    val city = state.city
    binding.cityInfo.isVisible = city != null
    if (city == null) return
    binding.cityValue.text = city.name
    binding.countryValue.text = city.country
    binding.populationValue.text = getString(
      R.string.city_details_population_value,
      formatPopulation(city.population),
    )
  }

  // View не умеют читать AppTheme, поэтому те же дизайн-токены приходят через ViewAppTheme. Тема применяется
  // к каждой новой View: при переключении на тёмную тему Activity и View фрагмента пересоздаются
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

  // Экран рисуется edge-to-edge: Compose UI отступает от системных панелей сам через safeDrawingPadding, а View здесь
  private fun applyWindowInsets(root: View) {
    ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
      val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
      view.updatePadding(left = bars.left, top = bars.top, right = bars.right, bottom = bars.bottom)
      insets
    }
  }
}
