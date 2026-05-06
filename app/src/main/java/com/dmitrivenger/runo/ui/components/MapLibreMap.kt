package com.dmitrivenger.runo.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.dmitrivenger.runo.domain.model.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.geometry.LatLng as MLLatLng

// OSM raster tiles — no API key required.
// Attribution is embedded in the style and displayed by MapLibre automatically.
const val OSM_STYLE_JSON = """
{
  "version": 8,
  "sources": {
    "osm": {
      "type": "raster",
      "tiles": ["https://tile.openstreetmap.org/{z}/{x}/{y}.png"],
      "tileSize": 256,
      "attribution": "© <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors"
    }
  },
  "layers": [{
    "id": "osm",
    "type": "raster",
    "source": "osm"
  }]
}
"""

fun LatLng.toMapLibre() = MLLatLng(latitude, longitude)

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
    val mapView = remember { MapView(context) }

    AndroidView(factory = { mapView }, modifier = modifier)

    DisposableEffect(Unit) {
        mapView.onCreate(null)
        mapView.onStart()
        mapView.onResume()
        mapView.getMapAsync { map ->
            map.setStyle(Style.Builder().fromJson(OSM_STYLE_JSON)) { style ->
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
