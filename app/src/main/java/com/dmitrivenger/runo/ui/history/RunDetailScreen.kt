package com.dmitrivenger.runo.ui.history

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunDetailScreen(
    runId: Long,
    app: RunoApplication,
    onBack: () -> Unit,
) {
    var run by remember { mutableStateOf<Run?>(null) }
    LaunchedEffect(runId) { run = app.runRepository.getRunById(runId) }

    val r = run ?: return
    val date = SimpleDateFormat("EEEE, MMMM d, yyyy · h:mm a", Locale.getDefault())
        .format(Date(r.startTime))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Run Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Text(date, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Distance", "%.2f".format(r.distanceKm), "km",
                        MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    MetricCard("Time", r.formattedDuration(), modifier = Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard("Avg Pace", r.formattedPace().replace(" /km", ""), "/km",
                        modifier = Modifier.weight(1f))
                    MetricCard("Calories", "${r.caloriesBurned.toInt()}", "kcal",
                        MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                }
            }

            if (r.routePoints.size >= 2) {
                item {
                    Text("Route", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    DetailRouteMap(r.routePoints, modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(16.dp)))
                }
            }

            if (r.kmPaces.size >= 2) {
                item {
                    Text("Pace breakdown", style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(8.dp))
                    val sorted = r.kmPaces.entries.sortedBy { it.key }
                    Box(
                        Modifier.fillMaxWidth().height(140.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                    ) {
                        PaceChart(sorted.map { it.value },
                            lineColor = MaterialTheme.colorScheme.secondary)
                    }
                    Spacer(Modifier.height(8.dp))
                    sorted.forEach { (km, pace) ->
                        val m = (pace / 60).toInt()
                        val s = (pace % 60).toInt()
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("KM $km", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface)
                            Text("%d:%02d /km".format(m, s),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary)
                        }
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRouteMap(points: List<LatLng>, modifier: Modifier = Modifier) {
    val bounds = remember(points) {
        val b = LatLngBounds.Builder()
        points.forEach { b.include(it) }
        b.build()
    }
    val cam = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(bounds.center, 14f)
    }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cam,
        uiSettings = MapUiSettings(scrollGesturesEnabled = true, zoomControlsEnabled = false),
        properties = MapProperties(isMyLocationEnabled = false),
    ) {
        Polyline(points = points,
            color = DarkGreen,
            width = 10f, jointType = JointType.ROUND)
    }
}
