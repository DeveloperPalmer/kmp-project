package com.kmpproject.feature.map.domain.mapper

import com.kmpproject.feature.map.domain.entity.GeoPoint
import com.kmpproject.feature.map.domain.entity.MapViewport
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

/** Areas worth a request: the camera has stayed still for a moment and moved to a different area. */
@OptIn(FlowPreview::class)
fun Flow<MapViewport>.settledAreas(): Flow<MapViewport> {
  return debounce(SETTLE_TIMEOUT)
    .distinctUntilChanged { old, new -> old.isSameArea(new) }
}

/** Great-circle distance by the haversine formula. */
fun distanceMeters(from: GeoPoint, to: GeoPoint): Double {
  val latDelta = (to.lat - from.lat).toRadians()
  val lonDelta = (to.lon - from.lon).toRadians()
  val a = sin(latDelta / 2).pow(2) +
    cos(from.lat.toRadians()) *
    cos(to.lat.toRadians()) *
    sin(lonDelta / 2).pow(2)
  return 2 * EARTH_RADIUS_METERS * asin(sqrt(a))
}

/** A viewport centered at [center] whose radius reaches the farthest of the screen [corners]. */
fun viewportAround(center: GeoPoint, corners: List<GeoPoint>): MapViewport {
  return MapViewport(
    center = center,
    radiusMeters = corners.maxOf { corner -> distanceMeters(center, corner) },
  )
}

/**
 * Whether [other] shows practically the same cities, so they need no new request.
 *
 * The server returns only the most populated cities of an area, so a noticeably smaller radius
 * is a different area: zooming in has to bring the smaller cities.
 */
fun MapViewport.isSameArea(other: MapViewport): Boolean {
  val tolerance = radiusMeters * SAME_AREA_TOLERANCE
  return distanceMeters(center, other.center) <= tolerance && abs(radiusMeters - other.radiusMeters) <= tolerance
}

private fun Double.toRadians(): Double = this * PI / 180

private const val EARTH_RADIUS_METERS = 6_371_000.0
private const val SAME_AREA_TOLERANCE = 0.1
internal val SETTLE_TIMEOUT = 300.milliseconds
