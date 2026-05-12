package com.dmitrivenger.runo.ui.summary

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.domain.model.LatLng
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.ui.components.MapLibreMapView
import com.dmitrivenger.runo.ui.components.PaceChart
import com.dmitrivenger.runo.ui.components.RunoPrimaryButton
import com.dmitrivenger.runo.ui.components.buildRouteGeoJson
import com.dmitrivenger.runo.ui.components.toMapLibre
import com.dmitrivenger.runo.ui.theme.Brand_DeepGreen
import com.dmitrivenger.runo.ui.theme.Brand_White
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource

private const val ROUTE_COLOR      = "#0F3D2E"   // Brand_DeepGreen
private const val ROUTE_GLOW_COLOR = "#A8C290"   // Brand_SoftGreenTint

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
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding(),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        // ── Celebration header ────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp),
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
                Spacer(Modifier.height(20.dp))
            }
        }

        // ── 2×2 stats grid ────────────────────────────────────────────────────
        item {
            val avgSpeedKmh = if (r.averagePaceSecondsPerKm > 0f)
                "%.1f".format(3600f / r.averagePaceSecondsPerKm) else "--.-"

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SummaryHeroCard(
                        label = "DISTANCE",
                        value = "%.2f".format(r.distanceKm),
                        unit = "km",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .scale(heroScale)
                            .alpha(heroAlpha),
                    )
                    SummaryStatCard(
                        label = "TIME",
                        value = r.formattedDuration(),
                        unit = "",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .alpha(contentAlpha),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SummaryStatCard(
                        label = "AVG SPEED",
                        value = avgSpeedKmh,
                        unit = "km/h",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .alpha(contentAlpha),
                    )
                    SummaryStatCard(
                        label = "CALORIES",
                        value = "${r.caloriesBurned.toInt()}",
                        unit = "kcal",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .alpha(contentAlpha),
                    )
                }
            }
        }

        // ── Route map ─────────────────────────────────────────────────────────
        if (r.routePoints.size >= 2) {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 20.dp)
                        .alpha(contentAlpha),
                ) {
                    SectionHeading("Route")
                    Spacer(Modifier.height(10.dp))
                    RouteMapView(
                        points = r.routePoints,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(16.dp)),
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
private fun SummaryHeroCard(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Brand_DeepGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Brand_White.copy(alpha = 0.65f),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = Brand_White,
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelMedium,
                color = Brand_White.copy(alpha = 0.65f),
            )
        }
    }
}

@Composable
private fun SummaryStatCard(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (unit.isNotEmpty()) {
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
                    PropertyFactory.lineColor(ROUTE_GLOW_COLOR),
                    PropertyFactory.lineWidth(20f),
                    PropertyFactory.lineOpacity(0.35f),
                    PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                    PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                )
            )
            style.addLayer(
                LineLayer("route-line", "route").withProperties(
                    PropertyFactory.lineColor(ROUTE_COLOR),
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
