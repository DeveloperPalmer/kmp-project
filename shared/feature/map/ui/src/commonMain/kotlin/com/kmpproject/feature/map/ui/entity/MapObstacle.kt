package com.kmpproject.feature.map.ui.entity

import androidx.compose.runtime.Stable

/**
 * Something covering the map from below; [top] is in px of the map layer, [Float.POSITIVE_INFINITY]
 * while it takes no space.
 */
@Stable
internal interface MapObstacle {
  val top: Float
}
