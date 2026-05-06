package com.dmitrivenger.runo.ui.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.domain.model.LatLng
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.ui.components.MapLibreMapView
import com.dmitrivenger.runo.ui.components.MetricCard
import com.dmitrivenger.runo.ui.components.PaceChart
import com.dmitrivenger.runo.ui.components.buildRouteGeoJson
import com.dmitrivenger.runo.ui.components.toMapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource

@Composable
fun SummaryScreen(runId: Long, app: RunoApplication, onDone: () -> Unit) {
    var run by remember { mutableStateOf<Run?>(null) }
    LaunchedEffect(runId) { run = app.runRepository.getRunById(runId) }
    val r = run ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(24.dp)) {
                Spacer(Modifier.height(16.dp))
                Text("Run Complete", style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary)
                Text("Great effort! Here's how you did.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Distance", "%.2f".format(r.distanceKm), "km",
                        MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    MetricCard("Time", r.formattedDuration(), modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Avg Pace", r.formattedPace().replace(" /km", ""), "/km",
                        modifier = Modifier.weight(1f))
                    MetricCard("Calories", "${r.caloriesBurned.toInt()}", "kcal",
                        MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                }

                if (r.routePoints.size >= 2) {
                    Spacer(Modifier.height(24.dp))
                    Text("Route", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    RouteMapView(r.routePoints, modifier = Modifier
                        .fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(16.dp)))
                }

                if (r.kmPaces.size >= 2) {
                    Spacer(Modifier.height(24.dp))
                    Text("Pace per km", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    val paceValues = r.kmPaces.entries.sortedBy { it.key }.map { it.value }
                    Box(Modifier.fillMaxWidth().height(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp)
                    ) {
                        PaceChart(values = paceValues, lineColor = MaterialTheme.colorScheme.secondary)
                    }
                }

                Spacer(Modifier.height(32.dp))
                Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) { Text("Done", style = MaterialTheme.typography.titleMedium) }
            }
        }
    }
}

@Composable
private fun RouteMapView(points: List<LatLng>, modifier: Modifier = Modifier) {
    MapLibreMapView(
        modifier = modifier,
        onMapReady = { map, style ->
            val source = GeoJsonSource("route", buildRouteGeoJson(points))
            style.addSource(source)
            style.addLayer(
                LineLayer("route-layer", "route").withProperties(
                    PropertyFactory.lineColor("#1DB954"),
                    PropertyFactory.lineWidth(8f),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                )
            )
            val boundsBuilder = LatLngBounds.Builder()
            points.forEach { boundsBuilder.include(it.toMapLibre()) }
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 64))
        }
    )
}
