package com.kmpproject.feature.map.ui.function

import androidx.collection.LongObjectMap
import androidx.collection.LongSet
import androidx.collection.MutableLongSet
import androidx.collection.emptyLongSet
import com.kmpproject.feature.map.ui.entity.MapPin

internal data class PinsDiff(
  val added: List<MapPin>,
  val removedIds: LongSet,
) {
  val isEmpty: Boolean
    get() = added.isEmpty() && removedIds.isEmpty()
}

/** What the map has to change to show [pins] instead of the [shown] ones; pins already shown stay as they are. */
internal fun pinsDiff(shown: LongObjectMap<*>, pins: List<MapPin>): PinsDiff {
  val added = pins.filterNot { pin -> pin.id in shown }
  // Pins only accumulate, so the gone ones are looked for only when some shown pin is missing
  if (shown.size + added.size == pins.size) {
    return PinsDiff(added, emptyLongSet())
  }
  val pinIds = MutableLongSet(pins.size)
  pins.forEach { pin -> pinIds += pin.id }
  val removedIds = MutableLongSet()
  shown.forEachKey { id ->
    if (id !in pinIds) removedIds += id
  }
  return PinsDiff(added, removedIds)
}
