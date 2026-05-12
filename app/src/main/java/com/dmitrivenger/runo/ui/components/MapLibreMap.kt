package com.dmitrivenger.runo.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dmitrivenger.runo.domain.model.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.geometry.LatLng as MLLatLng

// OpenFreeMap — free, no API key required.
// Bright:   natural, terrain-aware style matching Runo's earthy green aesthetic.
// Liberty:  clean dark-toned style (matches Runo dark design).
// Attribution is automatically displayed by MapLibre.
private const val MAP_STYLE_LIGHT = "https://tiles.openfreemap.org/styles/bright"
private const val MAP_STYLE_DARK  = "https://tiles.openfreemap.org/styles/liberty"

fun LatLng.toMapLibre() = MLLatLng(latitude, longitude)

fun buildPointGeoJson(point: LatLng): String =
    """{"type":"Feature","geometry":{"type":"Point","coordinates":[${point.longitude},${point.latitude}]},"properties":{}}"""

fun buildRouteGeoJson(points: List<LatLng>): String {
    if (points.size < 2) return """{"type":"FeatureCollection","features":[]}"""
    val coords = points.joinToString(",") { "[${it.longitude},${it.latitude}]" }
    return """{"type":"Feature","geometry":{"type":"LineString","coordinates":[$coords]},"properties":{}}"""
}

@Composable
fun MapLibreMapView(
    modifier: Modifier = Modifier,
    onMapReady: (MapLibreMap, Style) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val styleUri = if (isDark) MAP_STYLE_DARK else MAP_STYLE_LIGHT

    val mapView = remember { MapView(context) }

    AndroidView(factory = { mapView }, modifier = modifier)

    DisposableEffect(styleUri) {
        mapView.onCreate(null)
        mapView.onStart()
        mapView.onResume()
        mapView.getMapAsync { map ->
            map.setStyle(Style.Builder().fromUri(styleUri)) { style ->
                onMapReady(map, style)
            }
        }
        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }
}
