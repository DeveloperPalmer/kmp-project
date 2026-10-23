package com.kmpbaseproject.feature.cities.ui.screen.cities.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kmpbaseproject.feature.cities.domain.entity.City
import com.kmpbaseproject.feature.cities.ui.views.databinding.ItemCityBinding
import com.kmpbaseproject.uikit.view.ViewAppTheme

/**
 * Адаптер — мост между данными и RecyclerView: он знает, сколько строк в списке, создаёт держатели строк
 * и заполняет их данными. Сам RecyclerView данных не видит, он только спрашивает адаптер.
 *
 * PagingDataAdapter устроен как ListAdapter, только список приходит страницами в PagingData. Новый список он
 * сравнивает со старым через DiffUtil в фоновом потоке и сообщает RecyclerView точечные изменения: что вставлено,
 * удалено, сдвинуто или изменилось. Поэтому notifyDataSetChanged здесь не нужен. А когда пользователь докручивает
 * до конца загруженного, адаптер сам просит Paging загрузить следующую страницу
 */
internal class CitiesAdapter(
  private val theme: ViewAppTheme,
  private val onCityClick: (City) -> Unit,
) : PagingDataAdapter<City, CityViewHolder>(CityDiffCallback) {
  // Вызывается, только когда в пуле нет свободного держателя: создать View строки и держатель для неё
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
    // parent нужен ради layout_* корня строки, иначе пропадёт match_parent. false — строку в список добавит
    // сам RecyclerView
    val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return CityViewHolder(binding, theme, onCityClick)
  }

  // Вызывается при каждом появлении строки на экране, в том числе для переиспользованного держателя. Здесь только
  // заполняют держатель данными: этот метод вызывается постоянно при прокрутке, и тяжёлая работа здесь даёт рывки
  override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
    // getItem ещё и сообщает Paging, какую позицию видит пользователь: по ней он решает, когда грузить дальше.
    // null бывает только у placeholder'ов, а они выключены в PagingConfig
    val city = getItem(position) ?: return
    holder.bind(city)
  }
}

// DiffUtil сравнивает старый и новый списки в два шага.
private object CityDiffCallback : DiffUtil.ItemCallback<City>() {
  // areItemsTheSame — тот же ли это элемент: по нему DiffUtil понимает,
  // что строка сдвинулась, а не удалена и вставлена заново.
  override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
    return oldItem.id == newItem.id
  }

  // areContentsTheSame — изменилось ли содержимое того же элемента:
  // если да, строку перепривяжут
  override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
    return oldItem == newItem
  }
}
