package com.dmitrivenger.runo.ui.summary

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.domain.model.LatLng
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.ui.components.MapLibreMapView
import com.dmitrivenger.runo.ui.components.MetricCard
import com.dmitrivenger.runo.ui.components.PaceChart
import com.dmitrivenger.runo.ui.components.RunoPrimaryButton
import com.dmitrivenger.runo.ui.components.buildRouteGeoJson
import com.dmitrivenger.runo.ui.components.toMapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource

private const val BRAND_GREEN = "#22C55E"

@Composable
fun SummaryScreen(runId: Long, app: RunoApplication, onDone: () -> Unit) {
    var run by remember { mutableStateOf<Run?>(null) }
    LaunchedEffect(runId) { run = app.runRepository.getRunById(runId) }
    val r = run ?: return

    var animate by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animate = true }

    val heroScale by animateFloatAsState(
        targetValue = if (animate) 1f else 0.7f,
        animationSpec = tween(500),
        label = "heroScale",
    )
    val heroAlpha by animateFloatAsState(
        targetValue = if (animate) 1f else 0f,
        animationSpec = tween(400),
        label = "heroAlpha",
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (animate) 1f else 0f,
        animationSpec = tween(400, delayMillis = 250),
        label = "contentAlpha",
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        // ── Celebration header ────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 52.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Run Complete",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.alpha(heroAlpha),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = motivationalMessage(r.distanceKm),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.alpha(heroAlpha),
                )
                Spacer(Modifier.height(24.dp))

                // Hero distance
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .scale(heroScale)
                        .alpha(heroAlpha),
                ) {
                    Text(
                        text = "%.2f".format(r.distanceKm),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "kilometres",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(28.dp))
            }
        }

        // ── Stats grid ────────────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .alpha(contentAlpha),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label = "Time",
                        value = r.formattedDuration(),
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label = "Pace",
                        value = r.formattedPace().replace(" /km", ""),
                        unit = "/km",
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(12.dp))
                MetricCard(
                    label = "Calories",
                    value = "${r.caloriesBurned.toInt()}",
                    unit = "kcal",
                    valueColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // ── Route map ─────────────────────────────────────────────────────────
        if (r.routePoints.size >= 2) {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 28.dp)
                        .alpha(contentAlpha),
                ) {
                    SectionHeading("Route")
                    Spacer(Modifier.height(10.dp))
                    RouteMapView(
                        points = r.routePoints,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(20.dp)),
                    )
                }
            }
        }

        // ── Pace chart ────────────────────────────────────────────────────────
        if (r.kmPaces.size >= 2) {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 28.dp)
                        .alpha(contentAlpha),
                ) {
                    SectionHeading("Pace per km")
                    Spacer(Modifier.height(10.dp))
                    val paceValues = r.kmPaces.entries.sortedBy { it.key }.map { it.value }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(16.dp),
                    ) {
                        PaceChart(
                            values = paceValues,
                            lineColor = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        // ── Done button ───────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 36.dp)
                    .alpha(contentAlpha),
            ) {
                RunoPrimaryButton(text = "Done", onClick = onDone)
            }
        }
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun RouteMapView(points: List<LatLng>, modifier: Modifier = Modifier) {
    MapLibreMapView(
        modifier = modifier,
        onMapReady = { map, style ->
            val source = GeoJsonSource("route", buildRouteGeoJson(points))
            style.addSource(source)
            style.addLayer(
                LineLayer("route-glow", "route").withProperties(
                    PropertyFactory.lineColor(BRAND_GREEN),
                    PropertyFactory.lineWidth(14f),
                    PropertyFactory.lineOpacity(0.25f),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                )
            )
            style.addLayer(
                LineLayer("route-line", "route").withProperties(
                    PropertyFactory.lineColor(BRAND_GREEN),
                    PropertyFactory.lineWidth(5f),
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

private fun motivationalMessage(km: Float): String = when {
    km < 1f  -> "Every step counts — great start!"
    km < 3f  -> "Solid effort. Keep building that habit."
    km < 5f  -> "Nice work — you're finding your stride."
    km < 10f -> "Strong run! You're getting faster."
    else     -> "Incredible distance — that's serious running."
}
