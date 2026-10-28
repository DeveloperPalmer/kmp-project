package com.kmpproject.feature.cities.ui.screen.cities.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kmpproject.feature.cities.domain.entity.City
import com.kmpproject.feature.cities.ui.views.R
import com.kmpproject.feature.cities.ui.views.databinding.ItemCityBinding
import com.kmpproject.feature.cities.ui.views.databinding.ItemCityHeaderBinding
import com.kmpproject.uikit.view.ViewAppTheme

/**
 * Адаптер — мост между данными и RecyclerView: он знает, сколько строк в списке, создаёт держатели строк
 * и заполняет их данными. Сам RecyclerView данных не видит, он только спрашивает адаптер.
 *
 * PagingDataAdapter устроен как ListAdapter, только список приходит страницами в PagingData. Новый список он
 * сравнивает со старым через DiffUtil в фоновом потоке и сообщает RecyclerView точечные изменения: что вставлено,
 * удалено, сдвинуто или изменилось. Поэтому notifyDataSetChanged здесь не нужен. А когда пользователь докручивает
 * до конца загруженного, адаптер сам просит Paging загрузить следующую страницу.
 *
 * Строки двух видов — города и заголовки групп, — поэтому держатель общего типа RecyclerView.ViewHolder,
 * а нужный класс выбирается по viewType
 */
internal class CitiesAdapter(
  private val theme: ViewAppTheme,
  private val onCityClick: (City) -> Unit,
) : PagingDataAdapter<CityListItem, RecyclerView.ViewHolder>(CityListItemDiffCallback) {
  // Вид строки на этой позиции. RecyclerView спрашивает его раньше onCreateViewHolder и держит в пуле отдельную
  // очередь держателей на каждый вид: ушедший за край заголовок достанется только другому заголовку.
  // Значение — любое Int, лишь бы разное для разных видов; id разметки удобен тем, что уже уникален.
  // peek, а не getItem: peek не сообщает Paging, что позицию смотрят, это сделает getItem в onBindViewHolder
  override fun getItemViewType(position: Int): Int {
    return when (peek(position)) {
      is CityListItem.GroupHeader -> {
        R.layout.item_city_header
      }
      is CityListItem.CityRow, null -> {
        R.layout.item_city
      }
    }
  }

  // Вызывается, только когда в пуле нет свободного держателя этого вида:
  // создать View строки и держатель для неё
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    // parent нужен ради layout_* корня строки, иначе пропадёт match_parent. false — строку в список добавит
    // сам RecyclerView
    return when (viewType) {
      R.layout.item_city_header -> {
        val inflater = ItemCityHeaderBinding.inflate(inflater, parent, false)
        CityHeaderViewHolder(inflater, theme)
      }
      R.layout.item_city -> {
        val inflater = ItemCityBinding.inflate(inflater, parent, false)
        CityItemViewHolder(inflater, theme, onCityClick)
      }
      else -> {
        error("Unknown view type $viewType")
      }
    }
  }

  // Вызывается при каждом появлении строки на экране, в том числе для переиспользованного держателя. Здесь только
  // заполняют держатель данными: этот метод вызывается постоянно при прокрутке, и тяжёлая работа здесь даёт рывки
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    // getItem ещё и сообщает Paging, какую позицию видит пользователь: по ней он решает, когда грузить дальше.
    // null бывает только у placeholder'ов, а они выключены в PagingConfig.
    // Приведение безопасно: держатель создан в onCreateViewHolder для viewType этой же строки
    when (val item = getItem(position)) {
      is CityListItem.CityRow -> (holder as CityItemViewHolder).bind(item.city)
      is CityListItem.GroupHeader -> (holder as CityHeaderViewHolder).bind(item.group)
      null -> Unit
    }
  }

  // Город ли на этой позиции: разделитель рисуется только между двумя городами
  fun isCityAt(position: Int): Boolean {
    return peek(position) is CityListItem.CityRow
  }
}

// DiffUtil сравнивает старый и новый списки в два шага.
private object CityListItemDiffCallback : DiffUtil.ItemCallback<CityListItem>() {
  // areItemsTheSame — тот же ли это элемент: по нему DiffUtil понимает,
  // что строка сдвинулась, а не удалена и вставлена заново.
  // Город узнаётся по id, заголовок — по группе; строки разных видов одним элементом не бывают
  override fun areItemsTheSame(oldItem: CityListItem, newItem: CityListItem): Boolean {
    return when (oldItem) {
      is CityListItem.CityRow if (newItem is CityListItem.CityRow) -> {
        oldItem.city.id == newItem.city.id
      }
      is CityListItem.GroupHeader if (newItem is CityListItem.GroupHeader) -> {
        oldItem.group == newItem.group
      }
      else -> {
        false
      }
    }
  }

  // areContentsTheSame — изменилось ли содержимое того же элемента:
  // если да, строку перепривяжут
  override fun areContentsTheSame(oldItem: CityListItem, newItem: CityListItem): Boolean {
    return oldItem == newItem
  }
}
