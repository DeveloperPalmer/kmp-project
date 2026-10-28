package com.kmpproject.feature.map.ui.components

import androidx.compose.runtime.Stable
import com.kmpproject.feature.map.domain.entity.MapViewport
import com.kmpproject.feature.map.domain.mapper.viewportAround
import com.kmpproject.feature.map.ui.mapper.toGeoPoint
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.map.SizeChangedListener
import com.yandex.mapkit.mapview.MapView
import java.lang.ref.WeakReference

/**
 * Reports the viewport each time the camera stops and each time the map changes its size; the
 * size change also gives the first viewport, once the map is laid out. MapKit holds listeners
 * weakly, so the tracker listens itself.
 */
@Stable
internal class MapViewportTracker(
  private val mapView: MapView,
  private val onViewport: (MapViewport) -> Unit,
) : CameraListener, SizeChangedListener {
  private val cameraListener = WeakReference<CameraListener>(this)
  private val sizeListener = WeakReference<SizeChangedListener>(this)

  fun start() {
    mapView.mapWindow.map.addCameraListener(cameraListener)
    mapView.mapWindow.addSizeChangedListener(sizeListener)
  }

  fun stop() {
    mapView.mapWindow.map.removeCameraListener(cameraListener)
    mapView.mapWindow.removeSizeChangedListener(sizeListener)
  }

  override fun onCameraPositionChanged(
    map: Map,
    cameraPosition: CameraPosition,
    cameraUpdateReason: CameraUpdateReason,
    finished: Boolean,
  ) {
    if (finished) report()
  }

  override fun onMapWindowSizeChanged(mapWindow: MapWindow, width: Int, height: Int) {
    report()
  }

  private fun report() {
    val window = mapView.mapWindow
    if (window.width() == 0 || window.height() == 0) return
    val region = window.map.visibleRegion
    val viewport = viewportAround(
      center = window.map.cameraPosition.target.toGeoPoint(),
      corners = listOf(
        region.topLeft,
        region.topRight,
        region.bottomRight,
        region.bottomLeft
      ).map { corner -> corner.toGeoPoint() },
    )
    onViewport(viewport)
  }
}
