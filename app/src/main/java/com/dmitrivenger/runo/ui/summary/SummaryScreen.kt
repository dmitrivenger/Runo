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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.ui.components.MetricCard
import com.dmitrivenger.runo.ui.components.PaceChart
import com.dmitrivenger.runo.ui.theme.DarkGreen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun SummaryScreen(
    runId: Long,
    app: RunoApplication,
    onDone: () -> Unit,
) {
    var run by remember { mutableStateOf<Run?>(null) }

    LaunchedEffect(runId) {
        run = app.runRepository.getRunById(runId)
    }

    val r = run ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(24.dp)) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Run Complete",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Great effort! Here's how you did.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label = "Distance",
                        value = "%.2f".format(r.distanceKm),
                        unit = "km",
                        valueColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label = "Time",
                        value = r.formattedDuration(),
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MetricCard(
                        label = "Avg Pace",
                        value = r.formattedPace().replace(" /km", ""),
                        unit = "/km",
                        modifier = Modifier.weight(1f),
                    )
                    MetricCard(
                        label = "Calories",
                        value = "${r.caloriesBurned.toInt()}",
                        unit = "kcal",
                        valueColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                    )
                }

                if (r.routePoints.size >= 2) {
                    Spacer(Modifier.height(24.dp))
                    Text("Route", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    RouteMapView(r.routePoints, modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(16.dp)))
                }

                if (r.kmPaces.size >= 2) {
                    Spacer(Modifier.height(24.dp))
                    Text("Pace per km", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    val paceValues = r.kmPaces.entries.sortedBy { it.key }.map { it.value }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                    ) {
                        PaceChart(
                            values = paceValues,
                            lineColor = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text("Done", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun RouteMapView(points: List<LatLng>, modifier: Modifier = Modifier) {
    val bounds = remember(points) {
        val builder = LatLngBounds.Builder()
        points.forEach { builder.include(it) }
        builder.build()
    }
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bounds.center, 14f)
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraState,
        uiSettings = MapUiSettings(
            scrollGesturesEnabled = false,
            zoomGesturesEnabled = false,
            zoomControlsEnabled = false,
        ),
        properties = MapProperties(isMyLocationEnabled = false),
    ) {
        Polyline(
            points = points,
            color = DarkGreen,
            width = 10f,
            jointType = JointType.ROUND,
        )
    }
}
